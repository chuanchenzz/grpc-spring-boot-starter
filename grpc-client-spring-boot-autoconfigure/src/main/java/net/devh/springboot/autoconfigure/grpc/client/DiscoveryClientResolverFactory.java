package net.devh.springboot.autoconfigure.grpc.client;

import com.google.common.collect.Lists;
import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.internal.GrpcUtil;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;

public class DiscoveryClientResolverFactory extends BaseNameResolverProvider {
    private final DiscoveryClient client;
    private Attributes attributes;
    private List<NameResolver> nameResolverList;


    public DiscoveryClientResolverFactory(DiscoveryClient client, ServerConfigProperties serverConfigProperties) {
        this.client = client;
        this.nameResolverList = Lists.newArrayList();
        this.attributes = serverPropertiesResolver.resolve(serverConfigProperties);
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
        DiscoveryClientNameResolver discoveryClientNameResolver = new DiscoveryClientNameResolver(targetUri.toString(), client, GrpcUtil.TIMER_SERVICE, GrpcUtil.SHARED_CHANNEL_EXECUTOR, attributes);
        nameResolverList.add(discoveryClientNameResolver);
        return discoveryClientNameResolver;
    }

    @Override
    public String getDefaultScheme() {
        return "discoveryClient";
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    public List<NameResolver> getNameResolverList() {
        return nameResolverList;
    }
}
