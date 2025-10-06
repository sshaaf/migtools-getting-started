package com.redhat.mta.examples.springboot2.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring Boot 2 application using deprecated Netflix OSS components.
 * 
 * Deprecated features used:
 * - @EnableHystrix (deprecated in favor of Spring Cloud Circuit Breaker)
 * - @EnableZuulProxy (deprecated in favor of Spring Cloud Gateway)
 * - @RibbonClient (deprecated in favor of Spring Cloud LoadBalancer)
 */
@SpringBootApplication
@EnableHystrix
@EnableZuulProxy
@RibbonClient(name = "user-service")
public class SpringBoot2MigrationApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot2MigrationApplication.class, args);
    }
}
