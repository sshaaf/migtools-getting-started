package com.redhat.mta.examples.springboot2.migration.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration using deprecated Spring Boot 2 patterns.
 * 
 * Deprecated features:
 * - ConcurrentMapCacheManager (deprecated for production use)
 * - Manual CachesEndpoint configuration (auto-configured in Spring Boot 3)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // ConcurrentMapCacheManager is deprecated for production use
        // Should use Redis, Hazelcast, or other distributed cache
        return new ConcurrentMapCacheManager("users", "user");
    }

    // Note: CachesEndpoint configuration removed as it causes compilation issues
    // This demonstrates the kind of configuration that changes between Spring Boot versions
}
