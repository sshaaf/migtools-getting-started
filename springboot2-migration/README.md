# Spring Boot 2 Migration Example

This module demonstrates a Spring Boot 2 application that uses deprecated APIs and patterns that require migration when upgrading to Spring Boot 3. The application is designed to trigger static code analysis tools to identify migration issues.

## Deprecated Features Demonstrated

### 1. Netflix OSS Components (Deprecated)
- **Hystrix** (`@EnableHystrix`, `@HystrixCommand`) - Deprecated in favor of Spring Cloud Circuit Breaker
- **Zuul** (`@EnableZuulProxy`) - Deprecated in favor of Spring Cloud Gateway  
- **Ribbon** (`@RibbonClient`) - Deprecated in favor of Spring Cloud LoadBalancer

### 2. Spring Security Deprecated Patterns
- **WebSecurityConfigurerAdapter** - Deprecated in Spring Security 5.7+
- **NoOpPasswordEncoder** - Deprecated for security reasons
- **authorizeRequests()** - Deprecated in favor of `authorizeHttpRequests()`

### 3. Namespace Changes (Spring Boot 3)
- **javax.servlet** → **jakarta.servlet**
- **javax.persistence** → **jakarta.persistence**
- **javax.validation** → **jakarta.validation**

### 4. Deprecated Spring Patterns
- **Field injection** with `@Autowired` - Constructor injection preferred
- **Custom Actuator endpoints** using deprecated `@Endpoint` pattern
- **Optional.orElse(null)** anti-pattern

## Prerequisites

- Java 8+
- Maven 3.6+
- Docker and Docker Compose

## Running the Application

### 1. Start Database Services

```bash
cd springboot2-migration
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- pgAdmin on port 5050 (admin@example.com / admin)
- Redis on port 6379

### 2. Build and Run the Application

```bash
# From the project root
mvn clean install

# Run the Spring Boot 2 application
cd springboot2-migration
mvn spring-boot:run
```

The application will start on `http://localhost:8080/springboot2-migration`

### 3. Test the API Endpoints

```bash
# Get all users
curl http://localhost:8080/springboot2-migration/api/users

# Get user by ID
curl http://localhost:8080/springboot2-migration/api/users/1

# Create a new user
curl -X POST http://localhost:8080/springboot2-migration/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com"}'

# Check actuator endpoints
curl http://localhost:8080/springboot2-migration/actuator/health
curl http://localhost:8080/springboot2-migration/actuator/users
```

## Migration Analysis

This application is specifically designed to demonstrate common Spring Boot 2 patterns that need attention during migration to Spring Boot 3. Static code analysis tools should detect:

1. **Deprecated Netflix OSS dependencies** that need replacement
2. **Security configuration patterns** that require updates
3. **Namespace migrations** from javax to jakarta
4. **Anti-patterns** in dependency injection and error handling

## Database Access

- **PostgreSQL**: localhost:5432
  - Database: `springboot2_migration`
  - Username: `postgres`
  - Password: `postgres`

- **pgAdmin**: http://localhost:5050
  - Email: `admin@example.com`
  - Password: `admin`

## Stopping Services

```bash
docker-compose down
```

## Migration Path

When migrating this application to Spring Boot 3:

1. Replace Netflix OSS components with Spring Cloud alternatives
2. Update security configuration to use new patterns
3. Migrate namespace imports from javax to jakarta
4. Replace field injection with constructor injection
5. Update actuator endpoint configurations
6. Review and update any deprecated Spring Boot configuration properties
