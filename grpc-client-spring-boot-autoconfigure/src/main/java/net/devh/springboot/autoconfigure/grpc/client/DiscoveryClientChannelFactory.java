package net.devh.springboot.autoconfigure.grpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.grpc.*;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DiscoveryClientChannelFactory implements GrpcChannelFactory {
    private final GrpcChannelsProperties properties;
    private final LoadBalancer.Factory loadBalancerFactory;
    private final GlobalClientInterceptorRegistry globalClientInterceptorRegistry;
    private final DiscoveryClientResolverFactory nameResolverFactory;
    private HeartbeatMonitor monitor = new HeartbeatMonitor();

    public DiscoveryClientChannelFactory(GrpcChannelsProperties properties, LoadBalancer.Factory loadBalancerFactory,
                                         GlobalClientInterceptorRegistry globalClientInterceptorRegistry, DiscoveryClientResolverFactory nameResolverFactory) {
        this.properties = properties;
        this.loadBalancerFactory = loadBalancerFactory;
        this.globalClientInterceptorRegistry = globalClientInterceptorRegistry;
        this.nameResolverFactory = nameResolverFactory;
    }

    @EventListener(HeartbeatEvent.class)
    public void heartbeat(HeartbeatEvent event) {
        if (this.monitor.update(event.getValue())) {
            for (NameResolver nameResolver : nameResolverFactory.getNameResolverList()) {
                nameResolver.refresh();
            }
        }
    }

    @Override
    public Channel createChannel(String name) {
        return this.createChannel(name, null);
    }

    @Override
    public Channel createChannel(String name, List<ClientInterceptor> interceptors) {
        GrpcChannelProperties channelProperties = this.properties.getChannel(name);
        NettyChannelBuilder builder = NettyChannelBuilder.forTarget(name)
                .loadBalancerFactory(loadBalancerFactory)
                .nameResolverFactory(nameResolverFactory);
        if(channelProperties.isEnableRetry() && channelProperties.getMaxRetryAttempts() > 0){
            builder.enableRetry().maxRetryAttempts(channelProperties.getMaxRetryAttempts());
        }
        if (properties.getChannel(name).isPlaintext()) {
            builder.usePlaintext();
        }
        if (channelProperties.isEnableKeepAlive()) {
            builder.keepAliveWithoutCalls(channelProperties.isKeepAliveWithoutCalls())
                    .keepAliveTime(channelProperties.getKeepAliveTime(), TimeUnit.SECONDS)
                    .keepAliveTimeout(channelProperties.getKeepAliveTimeout(), TimeUnit.SECONDS);
        }
        if (channelProperties.getMaxInboundMessageSize() > 0) {
            builder.maxInboundMessageSize(channelProperties.getMaxInboundMessageSize());
        }
        Channel channel = builder.build();

        List<ClientInterceptor> globalInterceptorList = globalClientInterceptorRegistry.getClientInterceptors();
        Set<ClientInterceptor> interceptorSet = Sets.newHashSet();
        if (globalInterceptorList != null && !globalInterceptorList.isEmpty()) {
            interceptorSet.addAll(globalInterceptorList);
        }
        if (interceptors != null && !interceptors.isEmpty()) {
            interceptorSet.addAll(interceptors);
        }
        return ClientInterceptors.intercept(channel, Lists.newArrayList(interceptorSet));
    }
}
