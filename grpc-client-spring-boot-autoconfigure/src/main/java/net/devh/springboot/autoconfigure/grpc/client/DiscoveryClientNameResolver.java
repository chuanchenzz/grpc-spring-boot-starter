package net.devh.springboot.autoconfigure.grpc.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.internal.SharedResourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

import javax.annotation.concurrent.GuardedBy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class DiscoveryClientNameResolver extends NameResolver{
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryClientNameResolver.class);
    private final String name;
    private final DiscoveryClient client;
    private final SharedResourceHolder.Resource<ScheduledExecutorService> timerServiceResource;
    private final SharedResourceHolder.Resource<ExecutorService> executorResource;
    private Attributes attributes;
    @GuardedBy("this")
    private boolean shutdown;
    @GuardedBy("this")
    private ScheduledExecutorService timerService;
    @GuardedBy("this")
    private ExecutorService executor;
    @GuardedBy("this")
    private ScheduledFuture<?> resolutionTask;
    @GuardedBy("this")
    private boolean resolving;
    @GuardedBy("this")
    private Listener listener;
    @GuardedBy("this")
    private List<ServiceInstance> serviceInstanceList;

    public DiscoveryClientNameResolver(String name, DiscoveryClient client, SharedResourceHolder.Resource<ScheduledExecutorService> timerServiceResource,
                                       SharedResourceHolder.Resource<ExecutorService> executorResource, Attributes attributes) {
        this.name = name;
        this.client = client;
        this.timerServiceResource = timerServiceResource;
        this.executorResource = executorResource;
        this.serviceInstanceList = Lists.newArrayList();
        this.attributes = Preconditions.checkNotNull(attributes,"attributes");
    }

    @Override
    public final String getServiceAuthority() {
        return name;
    }

    @Override
    public final synchronized void start(Listener listener) {
        Preconditions.checkState(this.listener == null, "already started");
        timerService = SharedResourceHolder.get(timerServiceResource);
        this.listener = listener;
        executor = SharedResourceHolder.get(executorResource);
        this.listener = Preconditions.checkNotNull(listener, "listener");
        resolve();
    }

    @Override
    public final synchronized void refresh() {
        if (listener != null) {
            resolve();
        }
    }

    private final Runnable resolutionRunnable = new Runnable() {
        @Override
        public void run() {
            Listener savedListener;
            synchronized (DiscoveryClientNameResolver.this) {
                // If this task is started by refresh(), there might already be a scheduled task.
                if (resolutionTask != null) {
                    resolutionTask.cancel(false);
                    resolutionTask = null;
                }
                if (shutdown) {
                    return;
                }
                savedListener = listener;
                resolving = true;
            }
            try {
                List<ServiceInstance> newServiceInstanceList;
                try {
                    newServiceInstanceList = client.getInstances(name);
                } catch (Exception e) {
                    savedListener.onError(Status.UNAVAILABLE.withCause(e));
                    return;
                }

                if (!CollectionUtils.isEmpty(newServiceInstanceList)) {
                    if (isNeedToUpdateServiceInstanceList(newServiceInstanceList)) {
                        serviceInstanceList = newServiceInstanceList;
                    } else {
                        return;
                    }
                    List<EquivalentAddressGroup> equivalentAddressGroups = Lists.newArrayList();
                    for (ServiceInstance serviceInstance : serviceInstanceList) {
                        Map<String, String> metadata = serviceInstance.getMetadata();
                        if (metadata.get("gRPC") != null) {
                            Integer port = Integer.valueOf(metadata.get("gRPC"));
                            logger.info("Found gRPC server {} {}:{}", name, serviceInstance.getHost(), port);
                            EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(new InetSocketAddress(serviceInstance.getHost(), port),Attributes.EMPTY);
                            equivalentAddressGroups.add(addressGroup);
                        } else {
                            logger.error("Can not found gRPC server {}", name);
                        }
                    }
                    savedListener.onAddresses(equivalentAddressGroups, attributes);
                } else {
                    savedListener.onError(Status.UNAVAILABLE.withCause(new RuntimeException("UNAVAILABLE: NameResolver returned an empty list")));
                }
            } finally {
                synchronized (DiscoveryClientNameResolver.this) {
                    resolving = false;
                }
            }
        }
    };

    private boolean isNeedToUpdateServiceInstanceList(List<ServiceInstance> newServiceInstanceList) {
        if (serviceInstanceList.size() == newServiceInstanceList.size()) {
            for (ServiceInstance serviceInstance : serviceInstanceList) {
                boolean isSame = false;
                for (ServiceInstance newServiceInstance : newServiceInstanceList) {
                    if (newServiceInstance.getHost().equals(serviceInstance.getHost()) && newServiceInstance.getPort() == serviceInstance.getPort()) {
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    logger.info("Ready to update {} server info group list", name);
                    return true;
                }
            }
        } else {
            logger.info("Ready to update {} server info group list", name);
            return true;
        }
        return false;
    }

    private Map<String,Object> resolveServerConfig(){
        return Maps.newHashMap();
    }

    @GuardedBy("this")
    private void resolve() {
        if (resolving || shutdown) {
            return;
        }
        executor.execute(resolutionRunnable);
    }

    @Override
    public void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
        if (resolutionTask != null) {
            resolutionTask.cancel(false);
        }
        if (timerService != null) {
            timerService = SharedResourceHolder.release(timerServiceResource, timerService);
        }
        if (executor != null) {
            executor = SharedResourceHolder.release(executorResource, executor);
        }
    }
}
