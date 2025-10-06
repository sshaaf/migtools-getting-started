package com.redhat.mta.examples.spring.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.context.annotation.Bean;

/**
 * Spring MVC Configuration using deprecated WebMvcConfigurerAdapter.
 * 
 * Deprecated patterns for Spring Framework 6 migration:
 * - WebMvcConfigurerAdapter class deprecated in Spring 5.0
 * - Should implement WebMvcConfigurer interface directly
 * - Legacy CORS configuration patterns
 * - Deprecated view resolver configuration
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * Deprecated: WebMvcConfigurerAdapter.addCorsMappings()
     * In Spring 6: Use WebMvcConfigurer interface directly
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Legacy CORS configuration - deprecated approach
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
                
        // Multiple CORS mappings using deprecated patterns
        registry.addMapping("/public/**")
                .allowedOrigins("*")  // Deprecated: should be specific origins in Spring 6
                .allowedMethods("GET")
                .maxAge(86400);
    }

    /**
     * Deprecated: WebMvcConfigurerAdapter.addResourceHandlers()
     * Legacy static resource handling configuration
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Deprecated resource handler patterns
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(31556926);  // Deprecated: use CacheControl in Spring 6
                
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(31556926);
                
        // Legacy file upload handling
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(0);  // No caching for uploads
    }

    /**
     * Deprecated: WebMvcConfigurerAdapter.addInterceptors()
     * Legacy interceptor configuration
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Legacy interceptor registration patterns
        registry.addInterceptor(new LegacyAuthenticationInterceptor())
                .addPathPatterns("/admin/**", "/secure/**")
                .excludePathPatterns("/admin/login", "/admin/public/**");
                
        registry.addInterceptor(new DeprecatedLoggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/health", "/metrics");
    }

    /**
     * Deprecated: WebMvcConfigurerAdapter.configureViewResolvers()
     * Legacy JSP view resolver configuration
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // Deprecated JSP view resolver configuration
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        resolver.setOrder(1);
        
        registry.viewResolver(resolver);
    }

    /**
     * Deprecated bean configuration patterns
     * Legacy view resolver as bean - deprecated approach
     */
    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        resolver.setOrder(2);
        resolver.setCache(true);  // Deprecated caching configuration
        return resolver;
    }
    
    /**
     * Inner class demonstrating deprecated interceptor patterns
     */
    private static class LegacyAuthenticationInterceptor extends org.springframework.web.servlet.handler.HandlerInterceptorAdapter {
        // HandlerInterceptorAdapter is deprecated in Spring 5.3
        // Should implement HandlerInterceptor directly in Spring 6
        
        @Override
        public boolean preHandle(javax.servlet.http.HttpServletRequest request, 
                               javax.servlet.http.HttpServletResponse response, 
                               Object handler) throws Exception {
            // Legacy authentication logic using deprecated servlet API
            String authToken = request.getHeader("Authorization");
            if (authToken == null || !authToken.startsWith("Bearer ")) {
                response.sendError(401, "Unauthorized");
                return false;
            }
            return true;
        }
    }
    
    private static class DeprecatedLoggingInterceptor extends org.springframework.web.servlet.handler.HandlerInterceptorAdapter {
        @Override
        public boolean preHandle(javax.servlet.http.HttpServletRequest request, 
                               javax.servlet.http.HttpServletResponse response, 
                               Object handler) throws Exception {
            // Legacy logging patterns - deprecated approach
            long startTime = System.currentTimeMillis();
            request.setAttribute("startTime", startTime);
            return true;
        }
        
        @Override
        public void afterCompletion(javax.servlet.http.HttpServletRequest request, 
                                  javax.servlet.http.HttpServletResponse response, 
                                  Object handler, Exception ex) throws Exception {
            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                long endTime = System.currentTimeMillis();
                // Legacy logging without proper structured logging
                // Should use proper logging framework in Spring 6
            }
        }
    }
}

