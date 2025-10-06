# 🍃 Spring Boot to Quarkus Migration Example

This module demonstrates a comprehensive **Spring Boot 2.7.18** application that uses deprecated APIs and patterns requiring migration to **Quarkus 3.28.2**. The application simulates a production-ready e-commerce system and is specifically designed to trigger static code analysis tools to identify migration issues.

## 🎯 **Application Overview**

**Spring Boot to Quarkus Migration Example** is a full-featured Spring Boot application that demonstrates:
- 👤 **User Management** - User registration, authentication, and role-based access control
- 🛒 **Product Catalog** - Product management with categories, pricing, and inventory
- 📦 **Order Processing** - Order creation, status tracking, and order history
- 🔐 **Security Configuration** - Spring Security with method-level authorization
- 📊 **Data Access** - Spring Data JPA repositories with complex queries
- 🌐 **REST API** - RESTful endpoints with comprehensive request/response handling
- ✅ **Validation** - Bean validation with custom error handling
- 🗄️ **Caching** - Application-level caching with Spring Cache
- ⚡ **Async Processing** - Background task execution
- 🧪 **Testing** - Comprehensive unit and integration tests

## 🚨 **Deprecated Patterns Demonstrated**

This application intentionally uses **deprecated Spring Boot patterns** that need migration to Quarkus, based on the [Konveyor Spring Boot to Quarkus rulesets](https://github.com/konveyor/rulesets):

### **Dependencies & Build Configuration**
- ❌ **spring-boot-starter-parent** - Should be replaced with Quarkus BOM
- ❌ **spring-boot-starter-web** - Should be replaced with quarkus-rest-jackson
- ❌ **spring-boot-starter-data-jpa** - Should be replaced with quarkus-hibernate-orm-panache
- ❌ **spring-boot-starter-security** - Should be replaced with quarkus-security
- ❌ **spring-boot-starter-actuator** - Should be replaced with quarkus-smallrye-health and quarkus-micrometer
- ❌ **spring-boot-starter-test** - Should be replaced with quarkus-junit5
- ❌ **spring-boot-maven-plugin** - Should be replaced with quarkus-maven-plugin

### **Application Structure & Annotations**
- ❌ **@SpringBootApplication** - Not needed in Quarkus
- ❌ **SpringApplication.run()** - Replaced by Quarkus runtime
- ❌ **@EnableJpaRepositories** - Auto-configured in Quarkus
- ❌ **@EnableTransactionManagement** - Auto-configured in Quarkus
- ❌ **@EnableCaching** - Replaced with Quarkus Cache extension
- ❌ **@EnableAsync** - Replaced with Quarkus async configuration
- ❌ **@EnableScheduling** - Replaced with Quarkus Scheduler extension

### **Web Layer Patterns**
- ❌ **@RestController** - Should use **@Path**
- ❌ **@RequestMapping** - Should use **@GET/@POST/@PUT/@DELETE + @Path**
- ❌ **@GetMapping/@PostMapping/@PutMapping/@DeleteMapping** - Should use JAX-RS annotations
- ❌ **@PathVariable** - Should use **@PathParam**
- ❌ **@RequestParam** - Should use **@QueryParam**
- ❌ **@RequestBody** - Not needed in Quarkus (automatic)
- ❌ **@RequestHeader** - Should use **@HeaderParam**
- ❌ **@CookieValue** - Should use **@CookieParam**
- ❌ **ResponseEntity** - Should use JAX-RS **Response**
- ❌ **HttpStatus** - Should use **Response.Status**
- ❌ **@CrossOrigin** - Should use Quarkus CORS configuration

### **Data Access Patterns**
- ❌ **JpaRepository/CrudRepository** - Should use **PanacheRepository**
- ❌ **@Repository** - Should use **@ApplicationScoped**
- ❌ **@Query** - Should use Panache query methods
- ❌ **@Modifying** - Should use Panache update/delete methods
- ❌ **Pageable/Page/Sort** - Should use Panache paging/sorting
- ❌ **@Transactional (Spring)** - Should use **jakarta.transaction.Transactional**

### **Dependency Injection & Components**
- ❌ **@Autowired** - Should use **@Inject**
- ❌ **@Service** - Should use **@ApplicationScoped**
- ❌ **@Component** - Should use **@ApplicationScoped**
- ❌ **@Configuration** - Should use CDI producers or application.properties

### **Security Patterns**
- ❌ **WebSecurityConfigurerAdapter** - Should use application.properties configuration
- ❌ **@EnableWebSecurity** - Auto-configured in Quarkus
- ❌ **@PreAuthorize** - Should use **@RolesAllowed**
- ❌ **@Secured** - Should use **@RolesAllowed**
- ❌ **UserDetailsService** - Should use Quarkus identity providers

### **Exception Handling**
- ❌ **@ControllerAdvice** - Should use **@Provider**
- ❌ **@ExceptionHandler** - Should use **ExceptionMapper<T>**

### **Testing Patterns**
- ❌ **@SpringBootTest** - Should use **@QuarkusTest**
- ❌ **@WebMvcTest** - Should use **@QuarkusTest** with RestAssured
- ❌ **@DataJpaTest** - Should use **@QuarkusTest** with **@TestTransaction**
- ❌ **@MockBean** - Should use **@InjectMock**
- ❌ **MockMvc** - Should use **RestAssured**
- ❌ **TestRestTemplate** - Should use **RestAssured**

### **Configuration Properties**
- ❌ **server.*** - Should use **quarkus.http.***
- ❌ **spring.datasource.*** - Should use **quarkus.datasource.***
- ❌ **spring.jpa.*** - Should use **quarkus.hibernate-orm.***
- ❌ **spring.security.*** - Should use **quarkus.security.***
- ❌ **management.*** - Should use **quarkus.smallrye-health.*** and **quarkus.micrometer.***
- ❌ **logging.*** - Should use **quarkus.log.***
- ❌ **Profile-specific files** - Should use profile prefixes (%dev, %prod)

## 🏗️ **Project Structure**

```
springboot-to-quarkus-migration/
├── src/main/java/com/redhat/mta/examples/springboot/quarkus/
│   ├── SpringBootToQuarkusApplication.java     # Main application class
│   ├── config/
│   │   ├── ApplicationConfig.java              # Configuration classes
│   │   └── SecurityConfig.java                 # Security configuration
│   ├── controller/
│   │   └── UserController.java                 # REST controllers
│   ├── dto/
│   │   ├── UserCreateRequest.java              # Request DTOs
│   │   ├── UserUpdateRequest.java              # Update DTOs
│   │   └── UserResponse.java                   # Response DTOs
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java         # Exception handling
│   │   ├── ResourceNotFoundException.java      # Custom exceptions
│   │   └── DuplicateResourceException.java
│   ├── model/
│   │   ├── User.java                           # JPA entities
│   │   ├── Role.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── repository/
│   │   ├── UserRepository.java                 # Spring Data repositories
│   │   ├── RoleRepository.java
│   │   ├── ProductRepository.java
│   │   └── OrderRepository.java
│   └── service/
│       └── UserService.java                    # Business logic services
├── src/main/resources/
│   ├── application.properties                  # Main configuration
│   ├── application-dev.properties              # Dev profile
│   ├── application-prod.properties             # Prod profile
│   ├── banner.txt                              # Custom banner
│   └── data.sql                                # Sample data
├── src/test/java/
│   ├── controller/
│   │   └── UserControllerTest.java             # Controller tests
│   └── service/
│       └── UserServiceTest.java                # Service tests
└── pom.xml                                     # Maven configuration
```

## 🚀 **Getting Started**

### **Prerequisites**
- **Java 11+** (Java 17+ recommended for Quarkus 3.28.2)
- **Maven 3.8.6+** (required for Quarkus 3.x)
- **H2 Database** (embedded, included)

### **Running the Application**

1. **Clone and navigate to the module:**
   ```bash
   cd springboot-to-quarkus-migration
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Or with a specific profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### **Accessing the Application**

- **Base URL**: `http://localhost:8080/api`
- **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)
- **Health Check**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`

### **Authentication**

The application uses HTTP Basic authentication with these users:
- **admin/admin123** - ADMIN, USER roles
- **user/user123** - USER role
- **manager/manager123** - MANAGER, USER roles

### **API Examples**

```bash
# Get all users (requires authentication)
curl -u admin:admin123 http://localhost:8080/api/users

# Get user by ID
curl -u admin:admin123 http://localhost:8080/api/users/1

# Create new user (admin only)
curl -u admin:admin123 -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","email":"new@example.com","firstName":"New","lastName":"User","password":"password123"}'

# Get active users
curl -u admin:admin123 http://localhost:8080/api/users/active
```

## 🧪 **Running Tests**

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run with coverage
mvn test jacoco:report
```

## 🔍 **Migration Analysis**

To analyze this application with MTA/Konveyor:

```bash
# Using Konveyor CLI (when available)
konveyor analyze --input springboot-to-quarkus-migration/ --target quarkus3 --output reports/

# Using MTA CLI
mta-cli --input springboot-to-quarkus-migration/ --target quarkus3 --output reports/springboot-quarkus-analysis
```

## 📋 **Expected Migration Issues**

When analyzed with MTA, this application should trigger rules for:

1. **Dependencies** (8 rules) - Spring Boot starters → Quarkus extensions
2. **Annotations** (14 rules) - Spring annotations → JAX-RS/CDI annotations  
3. **Configuration** (9 rules) - application.properties format changes
4. **Web Layer** (10 rules) - Spring MVC → JAX-RS patterns
5. **Data Access** (10 rules) - Spring Data → Panache patterns
6. **Testing** (9 rules) - Spring Test → Quarkus Test patterns
7. **Security** (5+ rules) - Spring Security → Quarkus Security

**Total Expected Issues: 65+ migration points**

## 🎯 **Migration Priorities**

1. **MANDATORY** - Critical changes required for Quarkus compatibility
2. **OPTIONAL** - Recommended improvements for better Quarkus integration
3. **POTENTIAL** - Suggestions for optimization

## 🤝 **Contributing**

When contributing to this migration example:

1. **Preserve deprecated patterns** - Don't fix the Spring Boot patterns; they're intentional
2. **Add comprehensive examples** - Include edge cases and complex scenarios
3. **Document migration paths** - Add comments explaining Quarkus equivalents
4. **Test coverage** - Ensure all patterns are covered by tests
5. **No System.out.println** - Use proper logging instead

