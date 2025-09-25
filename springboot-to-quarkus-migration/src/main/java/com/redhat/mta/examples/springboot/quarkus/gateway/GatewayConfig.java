package com.redhat.mta.examples.springboot.quarkus.gateway;

// Removed MeterRegistry import for simplification
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
// Simplified imports - removed unused discovery and metrics imports
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Spring Cloud Gateway Configuration demonstrating patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace Spring Cloud Gateway with Quarkus reactive routes using Vert.x
 * - Replace RouteLocator with Quarkus Router configuration
 * - Replace Gateway filters with Quarkus HTTP filters
 * - Replace reactive CORS with Quarkus CORS configuration
 */
@Configuration
public class GatewayConfig {

    /**
     * Route configuration using RouteLocator
     * In Quarkus: Replace with Router bean and reactive routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User service routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "spring-cloud-gateway")
                                .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                                .retry(3)
                                .circuitBreaker(c -> c.setName("user-service-cb").setFallbackUri("/fallback/users")))
                        .uri("http://localhost:8080"))
                
                // Product service routes (simulated microservice)
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "product-service")
                                .stripPrefix(1)
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("http://localhost:8081"))
                
                // Order service routes with load balancing
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .rewritePath("/api/orders/(?<segment>.*)", "/orders/${segment}")
                                .addRequestParameter("source", "gateway"))
                        .uri("lb://order-service"))
                
                // Admin routes with security
                .route("admin-service", r -> r
                        .path("/admin/**")
                        .and()
                        .header("X-Admin-Token")
                        .filters(f -> f
                                .addRequestHeader("X-Admin-Gateway", "true"))
                        .uri("http://localhost:8082"))
                
                // WebSocket route
                .route("websocket-route", r -> r
                        .path("/ws/**")
                        .uri("ws://localhost:8083"))
                
                // Fallback route
                .route("fallback-route", r -> r
                        .path("/fallback/**")
                        .uri("http://localhost:8080/api/fallback"))
                
                .build();
    }

    /**
     * Custom Gateway Filter
     * In Quarkus: Replace with ContainerRequestFilter/ContainerResponseFilter
     */
    @Bean
    public CustomGatewayFilter customGatewayFilter() {
        return new CustomGatewayFilter();
    }

    /**
     * Global Gateway Filter
     * In Quarkus: Replace with Quarkus HTTP filters
     */
    @Bean
    public GlobalLoggingFilter globalLoggingFilter() {
        return new GlobalLoggingFilter();
    }

    /**
     * Rate Limiter configuration
     * In Quarkus: Replace with Quarkus Rate Limiting extension
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // This would typically connect to Redis
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Key resolver for rate limiting
     * In Quarkus: Replace with custom rate limiting key extraction
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
            return userId != null ? Mono.just(userId) : Mono.just("anonymous");
        };
    }

    /**
     * CORS configuration for reactive gateway
     * In Quarkus: Replace with quarkus.http.cors.* properties
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    // Note: Additional beans like DiscoveryClientRouteDefinitionLocator, 
    // WeightedRoutePredicateFactory, and GatewayMetricsFilter would be configured here
    // but are simplified for this migration example.
    // In Quarkus, these would be replaced with:
    // - Service discovery through Consul/Kubernetes integration
    // - Custom Router predicates
    // - Micrometer metrics integration
}
