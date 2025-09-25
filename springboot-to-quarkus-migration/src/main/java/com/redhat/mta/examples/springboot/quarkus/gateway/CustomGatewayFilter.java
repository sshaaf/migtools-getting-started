package com.redhat.mta.examples.springboot.quarkus.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Custom Gateway Filter demonstrating Spring Cloud Gateway patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace GatewayFilter with ContainerRequestFilter/ContainerResponseFilter
 * - Replace reactive ServerWebExchange with JAX-RS ContainerRequestContext/ContainerResponseContext
 * - Replace Mono<Void> return type with void
 */
public class CustomGatewayFilter implements GatewayFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomGatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Pre-processing: Add custom headers and log request
        String requestId = java.util.UUID.randomUUID().toString();
        exchange.getRequest().mutate()
                .header("X-Request-ID", requestId)
                .header("X-Gateway-Filter", "CustomGatewayFilter")
                .build();

        logger.info("Processing request {} to {}", requestId, exchange.getRequest().getURI());

        // Continue with the filter chain and add post-processing
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // Post-processing: Add response headers
                    exchange.getResponse().getHeaders().add("X-Response-ID", requestId);
                    exchange.getResponse().getHeaders().add("X-Processed-By", "Spring-Cloud-Gateway");
                    logger.info("Completed processing request {}", requestId);
                })
                .doOnError(throwable -> {
                    logger.error("Error processing request {}: {}", requestId, throwable.getMessage());
                });
    }
}
