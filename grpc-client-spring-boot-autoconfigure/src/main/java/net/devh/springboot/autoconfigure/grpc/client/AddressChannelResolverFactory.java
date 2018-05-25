package net.devh.springboot.autoconfigure.grpc.client;

import java.net.URI;

import javax.annotation.Nullable;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.internal.GrpcUtil;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
public class AddressChannelResolverFactory extends BaseNameResolverProvider {

    private final GrpcChannelsProperties properties;

    private final Attributes attributes;

    public AddressChannelResolverFactory(GrpcChannelsProperties properties, ServerConfigProperties serverProperties) {
        this.properties = properties;
        this.attributes = serverPropertiesResolver.resolve(serverProperties);
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, Attributes params) {
        return new AddressChannelNameResolver(targetUri.toString(), properties.getChannel(targetUri.toString()), this.attributes, GrpcUtil.SHARED_CHANNEL_EXECUTOR);
    }

    @Override
    public String getDefaultScheme() {
        return "address";
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }


}
