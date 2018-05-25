package net.devh.springboot.autoconfigure.grpc.client;

import io.grpc.LoadBalancer;
import io.grpc.util.RoundRobinLoadBalancerFactory;
import net.devh.springboot.autoconfigure.grpc.client.model.MetadataInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: chuanchen
 * Date: 5/23/18
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({GrpcChannelFactory.class})
public class GrpcClientAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "grpc.client.server-config")
    public ServerConfigProperties serverConfigProperties() {
        return new ServerConfigProperties();
    }

    @Bean
    @ConditionalOnMissingBean(GrpcChannelsProperties.class)
    public GrpcChannelsProperties grpcChannelsProperties() {
        return new GrpcChannelsProperties();
    }

    @Bean
    public GlobalClientInterceptorRegistry globalClientInterceptorRegistry(DiscoveryClient discoveryClient) {
        return new GlobalClientInterceptorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(LoadBalancer.Factory.class)
    public LoadBalancer.Factory grpcLoadBalancerFactory() {
        return RoundRobinLoadBalancerFactory.getInstance();
    }

    @Configuration
    @ConditionalOnMissingBean(GrpcChannelFactory.class)
    protected static class AddressGrpcClientAutoConfiguration {
        protected AddressGrpcClientAutoConfiguration() {
        }

        @Bean
        public AddressChannelResolverFactory channelResolverFactory(GrpcChannelsProperties channelsProperties, ServerConfigProperties serverConfigProperties) {
            return new AddressChannelResolverFactory(channelsProperties, serverConfigProperties);
        }

        @Bean
        public GrpcChannelFactory addressChannelFactory(GrpcChannelsProperties channelsProperties, LoadBalancer.Factory loadBalancerFactory, GlobalClientInterceptorRegistry globalClientInterceptorRegistry, AddressChannelResolverFactory channelResolverFactory) {
            return new AddressChannelFactory(channelsProperties, loadBalancerFactory, globalClientInterceptorRegistry, channelResolverFactory);
        }
    }

    @Configuration
    protected static class DiscoveryGrpcClientAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(DiscoveryClientResolverFactory.class)
        public DiscoveryClientResolverFactory grpcNameResolverFactory(DiscoveryClient discoveryClient, ServerConfigProperties serverConfigProperties) {
            return new DiscoveryClientResolverFactory(discoveryClient, serverConfigProperties);
        }

        @ConditionalOnMissingBean
        @Bean
        public GrpcChannelFactory discoveryClientChannelFactory(GrpcChannelsProperties grpcChannelsProperties, LoadBalancer.Factory loadBalancerFactory,
                                                                GlobalClientInterceptorRegistry globalClientInterceptorRegistry, DiscoveryClientResolverFactory nameResolverFactory) {
            return new DiscoveryClientChannelFactory(grpcChannelsProperties, loadBalancerFactory, globalClientInterceptorRegistry, nameResolverFactory);
        }

    }

    @Bean
    @ConditionalOnClass(GrpcClient.class)
    public GrpcClientBeanPostProcessor grpcClientBeanPostProcessor() {
        return new GrpcClientBeanPostProcessor();
    }

    @Configuration
    @ConditionalOnProperty(value = "spring.sleuth.scheduled.enabled", matchIfMissing = true)
    @ConditionalOnClass(Tracer.class)
    protected static class TraceClientAutoConfiguration {
        protected TraceClientAutoConfiguration() {
        }

        @Bean
        public BeanPostProcessor clientInterceptorPostProcessor(GlobalClientInterceptorRegistry registry) {
            return new ClientInterceptorPostProcessor(registry);
        }

        private static class ClientInterceptorPostProcessor implements BeanPostProcessor {

            private GlobalClientInterceptorRegistry registry;

            public ClientInterceptorPostProcessor(GlobalClientInterceptorRegistry registry) {
                this.registry = registry;
            }

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof Tracer) {
                    this.registry.addClientInterceptors(new TraceClientInterceptor((Tracer) bean, new MetadataInjector()));
                }
                return bean;
            }
        }
    }

}
