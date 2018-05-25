package net.devh.examples.grpc.cloud.config;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public Integer integer(DiscoveryClient discoveryClient){
        return 1;
    }
}
