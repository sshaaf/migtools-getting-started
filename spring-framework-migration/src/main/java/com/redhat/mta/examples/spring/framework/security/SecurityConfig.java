package com.redhat.mta.examples.spring.framework.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security Configuration using deprecated WebSecurityConfigurerAdapter.
 * 
 * Deprecated patterns for Spring Framework 6 migration:
 * - WebSecurityConfigurerAdapter deprecated in Spring Security 5.7
 * - authorizeRequests() deprecated in favor of authorizeHttpRequests()
 * - antMatchers() deprecated in favor of requestMatchers()
 * - NoOpPasswordEncoder deprecated for security reasons
 * - Legacy authentication configuration patterns
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Deprecated: WebSecurityConfigurerAdapter.configure(HttpSecurity)
     * Legacy HTTP security configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Deprecated CSRF configuration
            .authorizeRequests()  // Deprecated: use authorizeHttpRequests() in Spring Security 6
                .antMatchers("/", "/home", "/public/**").permitAll()  // Deprecated: use requestMatchers()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/v1/**").hasAuthority("API_ACCESS")
                .anyRequest().authenticated()
            .and()
            .formLogin()  // Deprecated form login configuration
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            .and()
            .logout()  // Deprecated logout configuration
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            .and()
            .sessionManagement()  // Deprecated session management
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry())
                .and()
            .and()
            .httpBasic();  // Deprecated HTTP Basic authentication
    }

    /**
     * Deprecated: WebSecurityConfigurerAdapter.configure(WebSecurity)
     * Legacy web security configuration
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // Deprecated web security ignoring patterns
        web.ignoring()
            .antMatchers("/css/**", "/js/**", "/images/**")  // Deprecated: use requestMatchers()
            .antMatchers("/webjars/**")
            .antMatchers("/favicon.ico")
            .antMatchers("/actuator/health", "/actuator/info");
    }

    /**
     * Deprecated: WebSecurityConfigurerAdapter.configure(AuthenticationManagerBuilder)
     * Legacy authentication manager configuration
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Deprecated in-memory authentication configuration
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password("admin123")  // Plain text password - deprecated security practice
            .roles("ADMIN", "USER")
            .and()
            .withUser("user")
            .password("user123")
            .roles("USER")
            .and()
            .withUser("api")
            .password("api123")
            .authorities("API_ACCESS", "READ_ONLY");

        // Deprecated JDBC authentication configuration
        auth.jdbcAuthentication()
            .dataSource(dataSource())
            .usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ?")
            .authoritiesByUsernameQuery("SELECT username, authority FROM authorities WHERE username = ?")
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Deprecated: NoOpPasswordEncoder
     * Security vulnerability - passwords stored in plain text
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // NoOpPasswordEncoder is deprecated and insecure
        // Should use BCryptPasswordEncoder or similar in production
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Deprecated UserDetailsService configuration
     * Legacy user management patterns
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        // Deprecated User.withDefaultPasswordEncoder()
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("admin123")
            .roles("ADMIN", "USER")
            .build();

        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("user123")
            .roles("USER")
            .build();

        UserDetails apiUser = User.withDefaultPasswordEncoder()
            .username("apiuser")
            .password("api123")
            .authorities("API_ACCESS")
            .build();

        return new InMemoryUserDetailsManager(admin, user, apiUser);
    }

    /**
     * Legacy session registry configuration
     */
    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }

    /**
     * Deprecated DataSource configuration for JDBC authentication
     */
    @Bean
    public javax.sql.DataSource dataSource() {
        // Legacy embedded database configuration
        org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder builder = 
            new org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder();
        return builder
            .setType(org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2)
            .addScript("classpath:security-schema.sql")
            .addScript("classpath:security-data.sql")
            .build();
    }

    /**
     * Deprecated custom authentication filter
     * Legacy filter configuration patterns
     */
    @Bean
    public LegacyAuthenticationFilter legacyAuthenticationFilter() throws Exception {
        LegacyAuthenticationFilter filter = new LegacyAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setFilterProcessesUrl("/api/authenticate");
        return filter;
    }

    /**
     * Inner class demonstrating deprecated authentication filter
     */
    private static class LegacyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
        
        @Override
        public org.springframework.security.core.Authentication attemptAuthentication(
                javax.servlet.http.HttpServletRequest request, 
                javax.servlet.http.HttpServletResponse response) {
            
            // Legacy authentication logic using deprecated servlet API
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String token = request.getHeader("X-Auth-Token");
            
            if (token != null && !token.isEmpty()) {
                // Legacy token-based authentication
                return processTokenAuthentication(token);
            }
            
            // Fall back to standard username/password authentication
            return super.attemptAuthentication(request, response);
        }
        
        private org.springframework.security.core.Authentication processTokenAuthentication(String token) {
            // Deprecated token processing logic
            // Should use proper JWT or OAuth2 in Spring Security 6
            if ("legacy-token-123".equals(token)) {
                return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    "tokenuser", null, 
                    java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                    )
                );
            }
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid token");
        }
    }
}
