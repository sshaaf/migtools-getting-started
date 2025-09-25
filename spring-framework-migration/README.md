# 🍃 Spring Framework 5 to 6 Migration Example

This module demonstrates a comprehensive **Spring Framework 5** application that uses deprecated APIs and patterns requiring migration to **Spring Framework 6**. The application simulates an enterprise user management system and is specifically designed to trigger static code analysis tools to identify migration issues.

## 🎯 **Application Overview**

**Spring Framework Migration Example** is a full-featured Spring 5 application that demonstrates:
- 👤 **User Management** - User registration, profiles, and authentication
- 🏢 **Department Management** - Organizational structure and user assignments
- 🔐 **Security Configuration** - Authentication and authorization patterns
- 📊 **Data Access** - JPA repositories and transaction management
- 🌐 **Web Layer** - MVC controllers and view resolution
- ⚡ **Async Processing** - Background task execution and scheduling
- 🗄️ **Caching** - Data caching and cache management

## 🚨 **Deprecated Patterns Demonstrated**

This application intentionally uses **deprecated Spring Framework 5 patterns** that need migration to Spring Framework 6, based on the [Konveyor Spring Framework rulesets](https://github.com/konveyor/rulesets/tree/main/default/generated/spring-framework):

### **Configuration & Setup**
- ❌ **WebMvcConfigurerAdapter** - Deprecated in Spring 5.0, removed in Spring 6
- ❌ **WebSecurityConfigurerAdapter** - Deprecated in Spring Security 5.7
- ❌ **javax.annotation** usage - Should be **jakarta.annotation** in Spring 6
- ❌ **javax.persistence** usage - Should be **jakarta.persistence** in Spring 6
- ❌ **javax.servlet** usage - Should be **jakarta.servlet** in Spring 6

### **Web Layer Issues**
- ❌ **@RequestMapping** without HTTP method - Should use specific mappings
- ❌ **HandlerInterceptorAdapter** - Deprecated, implement **HandlerInterceptor** directly
- ❌ **ModelAndView** legacy patterns - Should use modern response patterns
- ❌ **HttpServletRequest/Response** direct usage - Should use Spring abstractions
- ❌ **Legacy CORS configuration** - Should use declarative CORS

### **Security Issues**
- ❌ **WebSecurityConfigurerAdapter.configure()** - Deprecated configuration methods
- ❌ **authorizeRequests()** - Deprecated, use **authorizeHttpRequests()**
- ❌ **antMatchers()** - Deprecated, use **requestMatchers()**
- ❌ **NoOpPasswordEncoder** - Security vulnerability, use proper encoding
- ❌ **User.withDefaultPasswordEncoder()** - Deprecated security pattern

### **Data Access Issues**
- ❌ **@Query with positional parameters** - Should use named parameters
- ❌ **Legacy JPA configuration** - Should use modern JPA patterns
- ❌ **Complex repository method names** - Should use **@Query** or Criteria API
- ❌ **Manual transaction management** - Should use declarative transactions
- ❌ **Legacy pagination patterns** - Should use proper **Pageable** patterns

### **Service Layer Issues**
- ❌ **Field injection (@Autowired)** - Should use constructor injection
- ❌ **@PostConstruct/@PreDestroy** with javax - Should use jakarta annotations
- ❌ **Complex @Transactional** configuration - Should use simpler patterns
- ❌ **Legacy async patterns** - Should use **CompletableFuture** or reactive
- ❌ **Manual cache management** - Should use declarative caching

### **Validation & Binding Issues**
- ❌ **@InitBinder** legacy patterns - Should use modern validation
- ❌ **Custom property editors** - Should use **Converter** or **Formatter**
- ❌ **Manual validation logic** - Should use **Bean Validation**
- ❌ **Legacy exception handling** - Should use **@ControllerAdvice**

## 📁 **Project Structure**

```
spring-framework-migration/
├── src/main/java/com/redhat/mta/examples/spring/framework/
│   ├── config/
│   │   ├── ApplicationConfig.java      # Deprecated configuration patterns
│   │   ├── WebMvcConfig.java          # Deprecated WebMvcConfigurerAdapter
│   │   └── SecurityConfig.java        # Deprecated WebSecurityConfigurerAdapter
│   ├── controller/
│   │   └── UserController.java        # Deprecated MVC patterns
│   ├── service/
│   │   └── UserService.java           # Deprecated service patterns
│   ├── repository/
│   │   └── UserRepository.java        # Deprecated JPA patterns
│   └── security/
│       └── SecurityConfig.java        # Deprecated security patterns
└── pom.xml                            # Spring Framework 5 dependencies
```

## 🔧 **Technologies & Versions**

### **Spring Framework 5 Stack**
- **Spring Framework**: 5.3.23 (deprecated patterns)
- **Spring Security**: 5.7.5 (deprecated configurations)
- **Spring Data JPA**: 2.7.5 (deprecated repository patterns)
- **Java**: 8 (legacy version)
- **JPA**: 2.2 (javax.persistence)
- **Servlet API**: 4.0.1 (javax.servlet)

### **Migration Target: Spring Framework 6**
- **Spring Framework**: 6.x (modern patterns)
- **Spring Security**: 6.x (component-based security)
- **Spring Data JPA**: 3.x (jakarta.persistence)
- **Java**: 17+ (minimum requirement)
- **JPA**: 3.0+ (jakarta.persistence)
- **Servlet API**: 5.0+ (jakarta.servlet)

## 🚀 **Running the Application**

### **Prerequisites**
- Java 8+
- Maven 3.6+

### **Build and Run**
```bash
# Navigate to the module directory
cd spring-framework-migration

# Compile the application
mvn clean compile

# Package the application
mvn package

# Run tests (may show deprecation warnings - this is expected)
mvn test
```

### **Expected Compilation Results**
- ✅ **Compiles successfully** with Spring Framework 5
- ⚠️ **Shows deprecation warnings** for migration analysis
- ❌ **Would fail** with Spring Framework 6 without migration

## 📊 **Migration Analysis**

### **Static Analysis Tools**
This code is designed to be analyzed by:
- **Konveyor MTA** - Migration Toolkit for Applications
- **Spring Boot Migrator** - Automated migration tool
- **IDE Migration Assistants** - IntelliJ IDEA, Eclipse, VS Code

### **Expected Analysis Results**
```
🔍 Migration Issues Found: ~50+ violations
├── Configuration Issues: ~15 violations
├── Web Layer Issues: ~12 violations  
├── Security Issues: ~10 violations
├── Data Access Issues: ~8 violations
├── Service Layer Issues: ~6 violations
└── Validation Issues: ~4 violations
```

### **Key Migration Tasks**
1. **Update dependencies** to Spring Framework 6.x
2. **Replace WebMvcConfigurerAdapter** with WebMvcConfigurer interface
3. **Replace WebSecurityConfigurerAdapter** with component-based security
4. **Update javax.** imports to **jakarta.**
5. **Replace authorizeRequests()** with **authorizeHttpRequests()**
6. **Replace antMatchers()** with **requestMatchers()**
7. **Convert field injection** to constructor injection
8. **Update async patterns** to use CompletableFuture
9. **Modernize caching** configuration
10. **Update validation** patterns

## 🎯 **Migration Value**

This example demonstrates **real-world migration challenges**:

### **Before (Spring Framework 5)**
- ❌ Uses deprecated APIs and patterns
- ❌ Security vulnerabilities (NoOpPasswordEncoder)
- ❌ Complex configuration patterns
- ❌ Manual resource management
- ❌ Legacy servlet API usage

### **After (Spring Framework 6)**
- ✅ Modern, supported APIs
- ✅ Enhanced security patterns
- ✅ Simplified configuration
- ✅ Improved performance
- ✅ Jakarta EE compatibility
- ✅ Better maintainability

## 🔗 **References**

- [Spring Framework 6 Migration Guide](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x)
- [Spring Security 6 Migration Guide](https://docs.spring.io/spring-security/reference/migration/index.html)
- [Konveyor Spring Framework Rulesets](https://github.com/konveyor/rulesets/tree/main/default/generated/spring-framework)
- [Jakarta EE Migration Guide](https://jakarta.ee/resources/migration-guide/)

## ⚠️ **Important Notes**

- **This code is intentionally deprecated** - it demonstrates patterns that need migration
- **Do not use in production** - contains security vulnerabilities and deprecated patterns  
- **For migration analysis only** - designed to trigger static analysis tools
- **Expected compilation warnings** - deprecation warnings are part of the demonstration

This module provides **comprehensive migration examples** for Spring Framework 5 to 6 migration analysis! 🍃
