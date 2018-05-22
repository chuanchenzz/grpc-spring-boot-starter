package net.devh.springboot.autoconfigure.grpc.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.internal.GrpcAttributes;
import io.grpc.internal.GrpcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryClientResolverFactory extends BaseNameResolverProvider {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryClientResolverFactory.class);
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
