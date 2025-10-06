package com.redhat.mta.examples.springboot.quarkus.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

/**
 * Application Configuration demonstrating Spring configuration patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Configuration classes with application.properties or CDI producers
 * - Replace @EnableCaching with Quarkus Cache extension configuration
 * - Replace @EnableAsync with Quarkus async configuration
 * - Replace @EnableScheduling with Quarkus Scheduler extension
 * - Replace @EnableTransactionManagement with automatic Quarkus transaction support
 * - Replace WebMvcConfigurer with Quarkus HTTP configuration
 * - Replace CORS configuration with quarkus.http.cors.* properties
 */
@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class ApplicationConfig implements WebMvcConfigurer {

    /**
     * Cache Manager configuration
     * In Quarkus, this would be replaced with Cache extension configuration
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList("users", "products", "orders"));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    /**
     * Async executor configuration
     * In Quarkus, this would be configured through application.properties:
     * quarkus.thread-pool.core-threads=10
     * quarkus.thread-pool.max-threads=50
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * CORS configuration
     * In Quarkus, this would be replaced with application.properties:
     * quarkus.http.cors=true
     * quarkus.http.cors.origins=http://localhost:3000,http://localhost:8080
     * quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
     * quarkus.http.cors.methods=GET,PUT,POST,DELETE,PATCH,OPTIONS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}


