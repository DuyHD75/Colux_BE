package com.dcode.identity_service.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean(name ="userLoginCache")
    public CacheStore<String, Integer> userCache() {
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }

    /*@Bean(name ="registrationCache")
    public CacheStore<Long, String> registrationCache() {
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }*/
}
