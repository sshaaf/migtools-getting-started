package com.redhat.mta.examples.springboot.quarkus.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration demonstrating Spring Security patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace WebSecurityConfigurerAdapter with application.properties configuration
 * - Replace @EnableWebSecurity with Quarkus security extensions
 * - Replace @EnableGlobalMethodSecurity with Quarkus method security
 * - Replace HttpSecurity configuration with quarkus.http.auth.permission.* properties
 * - Replace UserDetailsService with Quarkus security identity providers
 * - Replace @PreAuthorize with @RolesAllowed
 * - Replace @Secured with @RolesAllowed
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // CORS configuration - will be replaced with quarkus.http.cors.* properties
            .cors().and()
            
            // CSRF configuration - typically disabled for REST APIs
            .csrf().disable()
            
            // Session management - stateless for REST APIs
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            
            // Authorization rules - will be replaced with quarkus.http.auth.permission.* properties
            .authorizeRequests()
                // Public endpoints
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/info").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                
                // API documentation endpoints (if using Swagger/OpenAPI)
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                
                // Admin only endpoints
                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                
                // User and Admin endpoints
                .antMatchers(HttpMethod.GET, "/api/users").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/users/**").hasAnyRole("USER", "ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
                .and()
            
            // HTTP Basic authentication - simple for demo purposes
            .httpBasic()
                .and()
            
            // Headers configuration
            .headers()
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000))
                .and()
            
            // H2 Console configuration (development only)
            .headers().frameOptions().sameOrigin();
    }

    /**
     * Password encoder bean - BCrypt is recommended
     * In Quarkus, this would be configured through application.properties
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * In-memory user details service for demo purposes
     * In Quarkus, this would be replaced with:
     * - quarkus.security.users.embedded.* properties
     * - Or custom IdentityProvider implementation
     * - Or integration with external identity providers (OIDC, LDAP, etc.)
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN", "USER")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("user123"))
                .roles("USER")
                .build();

        UserDetails manager = User.builder()
                .username("manager")
                .password(passwordEncoder().encode("manager123"))
                .roles("MANAGER", "USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user, manager);
    }
}
