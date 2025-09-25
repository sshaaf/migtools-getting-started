package com.redhat.mta.examples.spring.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Spring Application Configuration using deprecated patterns.
 * 
 * Deprecated patterns for Spring Framework 6 migration:
 * - javax.persistence usage (should be jakarta.persistence in Spring 6)
 * - Legacy configuration patterns
 * - Deprecated bean definition approaches
 * - Legacy transaction manager configuration
 * - Deprecated caching configuration
 * - Legacy async executor configuration
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)  // Deprecated: Explicit proxy configuration
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)  // Deprecated: Explicit proxy mode
@ComponentScan(basePackages = {
    "com.redhat.mta.examples.spring.framework.service",
    "com.redhat.mta.examples.spring.framework.repository",
    "com.redhat.mta.examples.spring.framework.controller"
})
public class ApplicationConfig {

    /**
     * Deprecated: Legacy DataSource configuration
     * Spring 6: Should use modern connection pool and configuration
     */
    @Bean
    public DataSource dataSource() {
        // Deprecated: Manual DataSource configuration
        org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = 
            new org.springframework.jdbc.datasource.DriverManagerDataSource();
        
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        
        return dataSource;
    }

    /**
     * Deprecated: Legacy EntityManagerFactory configuration
     * Spring 6: Should use modern JPA configuration patterns
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.redhat.mta.examples.spring.framework.repository");
        
        // Deprecated: Manual JPA vendor adapter configuration
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        em.setJpaVendorAdapter(vendorAdapter);
        
        // Deprecated: Manual JPA properties configuration
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.use_sql_comments", "true");
        jpaProperties.put("hibernate.jdbc.batch_size", "20");
        jpaProperties.put("hibernate.cache.use_second_level_cache", "false");
        em.setJpaProperties(jpaProperties);
        
        return em;
    }

    /**
     * Deprecated: Legacy transaction manager configuration
     * Spring 6: Should use simpler transaction management
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        // Deprecated: Manual JPA transaction manager setup
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        
        // Deprecated: Manual transaction manager configuration
        transactionManager.setRollbackOnCommitFailure(true);
        transactionManager.setDefaultTimeout(30);
        transactionManager.setNestedTransactionAllowed(true);
        
        return transactionManager;
    }

    /**
     * Deprecated: Legacy cache manager configuration
     * Spring 6: Should use modern caching solutions
     */
    @Bean
    public CacheManager cacheManager() {
        // Deprecated: Simple in-memory cache manager
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Deprecated: Manual cache configuration
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "users", "departments", "roles", "userStats", "departmentUsers"
        ));
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }

    /**
     * Deprecated: Legacy async executor configuration
     * Spring 6: Should use modern async configuration
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        // Deprecated: Manual thread pool configuration
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("SpringAsync-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        
        // Deprecated: Manual executor initialization
        executor.initialize();
        
        return executor;
    }

    /**
     * Deprecated: Legacy aspect configuration
     * Spring 6: Should use modern AOP configuration
     */
    @Bean
    public org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator aspectJAutoProxyCreator() {
        // Deprecated: Manual AspectJ proxy creator configuration
        org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator proxyCreator = 
            new org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator();
        
        proxyCreator.setProxyTargetClass(true);
        proxyCreator.setExposeProxy(true);
        
        return proxyCreator;
    }

    /**
     * Deprecated: Legacy validator configuration
     * Spring 6: Should use modern validation patterns
     */
    @Bean
    public org.springframework.validation.beanvalidation.LocalValidatorFactoryBean validator() {
        // Deprecated: Manual validator factory configuration
        org.springframework.validation.beanvalidation.LocalValidatorFactoryBean validatorFactory = 
            new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
        
        // Deprecated: Manual validation provider configuration
        validatorFactory.setProviderClass(org.hibernate.validator.HibernateValidator.class);
        
        return validatorFactory;
    }

    /**
     * Deprecated: Legacy message source configuration
     * Spring 6: Should use modern internationalization patterns
     */
    @Bean
    public org.springframework.context.MessageSource messageSource() {
        // Deprecated: Manual resource bundle message source
        org.springframework.context.support.ResourceBundleMessageSource messageSource = 
            new org.springframework.context.support.ResourceBundleMessageSource();
        
        messageSource.setBasenames("messages", "validation", "errors");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(300);
        messageSource.setFallbackToSystemLocale(false);
        
        return messageSource;
    }

    /**
     * Deprecated: Legacy multipart resolver configuration
     * Spring 6: Should use modern file upload handling
     */
    @Bean
    public org.springframework.web.multipart.MultipartResolver multipartResolver() {
        // Deprecated: Commons multipart resolver
        org.springframework.web.multipart.commons.CommonsMultipartResolver resolver = 
            new org.springframework.web.multipart.commons.CommonsMultipartResolver();
        
        // Deprecated: Manual multipart configuration
        resolver.setMaxUploadSize(5242880); // 5MB
        resolver.setMaxInMemorySize(1048576); // 1MB
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);
        
        return resolver;
    }

    /**
     * Deprecated: Legacy exception resolver configuration
     * Spring 6: Should use modern exception handling
     */
    @Bean
    public org.springframework.web.servlet.HandlerExceptionResolver handlerExceptionResolver() {
        // Deprecated: Manual exception resolver configuration
        org.springframework.web.servlet.handler.SimpleMappingExceptionResolver resolver = 
            new org.springframework.web.servlet.handler.SimpleMappingExceptionResolver();
        
        // Deprecated: Manual exception mapping
        Properties exceptionMappings = new Properties();
        exceptionMappings.put("UserNotFoundException", "error/user-not-found");
        exceptionMappings.put("AccessDeniedException", "error/access-denied");
        exceptionMappings.put("DataAccessException", "error/database-error");
        resolver.setExceptionMappings(exceptionMappings);
        
        resolver.setDefaultErrorView("error/general");
        resolver.setDefaultStatusCode(500);
        resolver.setWarnLogCategory("warn");
        
        return resolver;
    }

    /**
     * Deprecated: Legacy property source configuration
     * Spring 6: Should use modern configuration management
     */
    @Bean
    public static org.springframework.context.support.PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        // Deprecated: Manual property placeholder configuration
        org.springframework.context.support.PropertySourcesPlaceholderConfigurer configurer = 
            new org.springframework.context.support.PropertySourcesPlaceholderConfigurer();
        
        // Deprecated: Manual property file location
        configurer.setLocation(new org.springframework.core.io.ClassPathResource("application.properties"));
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setIgnoreResourceNotFound(false);
        
        return configurer;
    }
}
