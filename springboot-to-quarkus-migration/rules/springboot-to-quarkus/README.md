# Spring Boot to Quarkus Migration Ruleset

This comprehensive ruleset provides guidance for migrating Spring Boot applications to Quarkus. It covers all major aspects of the migration process including dependencies, annotations, configuration, web layer, data access, testing, and security.

## Overview

The ruleset is organized into the following categories:

1. **Dependencies Migration** (`01-springboot-dependencies-to-quarkus.yaml`)
2. **Annotations Migration** (`02-springboot-annotations-to-quarkus.yaml`)
3. **Configuration Migration** (`03-springboot-configuration-to-quarkus.yaml`)
4. **Web Layer Migration** (`04-springboot-web-to-quarkus.yaml`)
5. **Data Access Migration** (`05-springboot-data-to-quarkus.yaml`)
6. **Testing Migration** (`06-springboot-testing-to-quarkus.yaml`)
7. **Security Migration** (`07-springboot-security-to-quarkus.yaml`)
8. **Spring Cloud Gateway Migration** (`08-spring-gateway-to-quarkus.yaml`)
9. **RESTful Services Migration** (`09-spring-restful-to-quarkus.yaml`)
10. **Persistence Layer Migration** (`10-spring-persistence-to-quarkus.yaml`)
11. **Event-Driven Services Migration** (`11-spring-events-to-quarkus.yaml`)
12. **Cloud-Native Applications Migration** (`12-spring-cloud-native-to-quarkus.yaml`)
13. **Reactive Programming Migration** (`13-spring-reactive-to-quarkus.yaml`)

## Migration Categories

### 1. Dependencies Migration

Covers the transformation of Maven dependencies from Spring Boot to Quarkus:

- Spring Boot Parent POM → Quarkus BOM
- Spring Boot Starters → Quarkus Extensions
- Spring Boot Maven Plugin → Quarkus Maven Plugin
- DevTools → Quarkus Dev Mode

**Key transformations:**
- `spring-boot-starter-web` → `quarkus-resteasy-reactive-jackson`
- `spring-boot-starter-data-jpa` → `quarkus-hibernate-orm-panache`
- `spring-boot-starter-test` → `quarkus-junit5` + `rest-assured`
- `spring-boot-starter-security` → `quarkus-security`
- `spring-boot-starter-actuator` → `quarkus-smallrye-health` + `quarkus-micrometer`

### 2. Annotations Migration

Transforms Spring annotations to their Quarkus/CDI/JAX-RS equivalents:

**Web Layer:**
- `@RestController` → `@Path`
- `@RequestMapping` → `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@PathVariable` → `@PathParam`
- `@RequestParam` → `@QueryParam`
- `@RequestBody` → (removed, automatic deserialization)

**Dependency Injection:**
- `@Autowired` → `@Inject`
- `@Service` → `@ApplicationScoped`
- `@Repository` → `@ApplicationScoped`
- `@Component` → `@ApplicationScoped`

### 3. Configuration Migration

Migrates Spring Boot configuration to Quarkus format:

**Property Mappings:**
- `server.port` → `quarkus.http.port`
- `server.servlet.context-path` → `quarkus.http.root-path`
- `spring.datasource.*` → `quarkus.datasource.*`
- `spring.jpa.*` → `quarkus.hibernate-orm.*`
- `logging.level.*` → `quarkus.log.category.*`
- `management.*` → `quarkus.smallrye-health.*` / `quarkus.smallrye-metrics.*`

**Profile Migration:**
- Profile-specific files (`application-{profile}.properties`) → Profile prefixes (`%{profile}.property=value`)

### 4. Web Layer Migration

Transforms Spring MVC components to JAX-RS:

- `ResponseEntity` → JAX-RS `Response`
- `@ExceptionHandler` → `@Provider` + `ExceptionMapper`
- `@ControllerAdvice` → `@Provider`
- `@CrossOrigin` → Quarkus CORS configuration
- `HttpStatus` → `Response.Status`
- `@RequestHeader` → `@HeaderParam`
- `@CookieValue` → `@CookieParam`

### 5. Data Access Migration

Migrates Spring Data JPA to Quarkus Panache:

- `JpaRepository` → `PanacheRepository`
- `CrudRepository` → `PanacheRepository`
- `@Query` → Panache query methods
- Spring `@Transactional` → `javax.transaction.Transactional`
- `Pageable` → `PanacheQuery` paging
- `Sort` → Panache `Sort`
- `@Modifying` queries → Panache update/delete methods

**Active Record Pattern Option:**
- JPA Entities can extend `PanacheEntity` for simplified Active Record pattern

### 6. Testing Migration

Transforms Spring Boot testing to Quarkus testing:

- `@SpringBootTest` → `@QuarkusTest`
- `@WebMvcTest` → `@QuarkusTest` + RestAssured
- `@DataJpaTest` → `@QuarkusTest` + `@TestTransaction`
- `MockMvc` → RestAssured
- `@MockBean` → `@InjectMock`
- `TestRestTemplate` → RestAssured
- `@TestPropertySource` → `@TestProfile`
- `@Sql` → Programmatic test data setup

### 7. Security Migration

Migrates Spring Security to Quarkus Security:

- `WebSecurityConfigurerAdapter` → Quarkus Security configuration
- `@PreAuthorize` → `@RolesAllowed`
- `@Secured` → `@RolesAllowed`
- Spring `Authentication` → Quarkus `SecurityIdentity`
- `@AuthenticationPrincipal` → `SecurityIdentity` injection
- `PasswordEncoder` → `BcryptUtil`
- `UserDetailsService` → `SecurityIdentityAugmentor` or `IdentityProvider`
- `@EnableGlobalMethodSecurity` → (enabled by default)

## Usage

To use this ruleset with Konveyor/Kai:

1. Ensure you have Konveyor/Kai installed and configured
2. Point the analysis to your Spring Boot application
3. Use the `springboot-to-quarkus` ruleset
4. Review the generated migration report
5. Follow the step-by-step migration guidance provided in each rule

## Migration Strategy

1. **Start with Dependencies**: Begin by updating your `pom.xml` file with Quarkus dependencies
2. **Configuration**: Migrate your `application.properties` files
3. **Annotations**: Update annotations in your source code
4. **Web Layer**: Transform controllers and web configuration
5. **Data Access**: Migrate repositories and data access code
6. **Testing**: Update your test classes
7. **Security**: Migrate security configuration and authentication logic

## Important Notes

- **Effort Levels**: Rules are categorized by effort (1-5 scale) to help prioritize migration tasks
- **Mandatory vs Optional**: Some rules are mandatory for a working migration, others are optional optimizations
- **Gradual Migration**: Some Spring Boot features can coexist with Quarkus during migration
- **Testing**: Thoroughly test each layer after migration
- **Performance**: Quarkus offers significant performance improvements, especially in startup time and memory usage

## Additional Resources

- [Quarkus Migration Guide from Spring Boot](https://quarkus.io/guides/spring-boot-properties)
- [Quarkus Spring Compatibility Extensions](https://quarkus.io/guides/spring-di)
- [Quarkus Getting Started Guide](https://quarkus.io/get-started/)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config)

### 8. Spring Cloud Gateway Migration

Transforms Spring Cloud Gateway components to Quarkus reactive routes:

- Spring Cloud Gateway dependencies → Quarkus Vert.x Web + Reactive Routes
- `RouteLocator` → `@Route` handlers with `RoutingContext`
- Gateway filters → Route filters with ordering
- Gateway predicates → Programmatic route matching
- YAML route configuration → Properties + Java route handlers
- Spring Cloud LoadBalancer → Quarkus Stork
- Rate limiting → Quarkus rate limiting extensions
- Circuit breakers → MicroProfile Fault Tolerance
- WebFlux security → Quarkus Security with OIDC
- WebClient → REST Client Reactive

**Key Features:**
- Reactive routing with Vert.x event loop
- Service discovery and load balancing with Stork
- Built-in fault tolerance and resilience patterns
- Comprehensive security integration
- Performance monitoring and metrics

### 9. RESTful Services Migration

Based on [Chapter 3 examples](https://github.com/quarkus-for-spring-developers/examples/tree/main/chapter-3), transforms Spring MVC REST components:

- `@RestController` + `@RequestMapping` → `@Path` + JAX-RS annotations
- `@ResponseStatus` → JAX-RS `Response` with status codes
- Spring validation → Bean Validation with automatic handling
- MockMvc testing → REST Assured testing
- Content negotiation → `@Produces`/`@Consumes` annotations
- HATEOAS → JAX-RS Link headers
- Async processing → Reactive types (`Uni`/`Multi`)
- CORS configuration → Quarkus CORS properties

### 10. Persistence Layer Migration

Based on [Chapter 4 examples](https://github.com/quarkus-for-spring-developers/examples/tree/main/chapter-4), transforms data access components:

- Spring Data JPA repositories → Panache repositories or Active Record pattern
- `@Transactional` → `javax.transaction.Transactional`
- JPA configuration → Quarkus Hibernate ORM configuration
- `@DataJpaTest` → `@QuarkusTest` with `@TestTransaction`
- JPA Specifications → Panache dynamic queries
- JPA Auditing → Panache lifecycle callbacks
- Custom repository implementations → Enhanced Panache methods

### 11. Event-Driven Services Migration

Based on [Chapter 5 examples](https://github.com/quarkus-for-spring-developers/examples/tree/main/chapter-5), transforms messaging components:

- Spring Kafka → Quarkus Reactive Messaging Kafka
- `@EventListener` → CDI `@Observes`
- `@KafkaListener` → `@Incoming` channels
- Kafka configuration → Reactive Messaging properties
- Spring Kafka testing → Quarkus Kafka testing with TestContainers
- `@Async` → `@Asynchronous` or reactive types

### 12. Cloud-Native Applications Migration

Based on [Chapter 6 examples](https://github.com/quarkus-for-spring-developers/examples/tree/main/chapter-6), transforms cloud-native components:

- Actuator health checks → MicroProfile Health checks (`@Liveness`/`@Readiness`)
- Actuator metrics → Micrometer metrics with Prometheus
- `@ConfigurationProperties` → `@ConfigProperties` or `@ConfigProperty`
- Docker configuration → Quarkus container image build
- Spring profiles → Quarkus configuration profiles
- Native image configuration → Quarkus native build optimization

### 13. Reactive Programming Migration

Transforms Spring WebFlux reactive components to Quarkus reactive stack:

- Spring WebFlux → Quarkus RESTEasy Reactive
- `Mono`/`Flux` → `Uni`/`Multi` (Mutiny)
- Spring Data R2DBC → Hibernate Reactive Panache
- `WebClient` → REST Client Reactive
- Reactor operators → Mutiny operators
- Reactive `@Transactional` → `@WithTransaction`

## Rule Statistics

- **Total Rules**: 90+ migration rules (duplicates removed)
- **Mandatory Rules**: 80+ rules
- **Optional Rules**: 10+ rules
- **Coverage Areas**: Dependencies, Annotations, Configuration, Web, Data, Testing, Security, Gateway, RESTful, Persistence, Events, Cloud-Native, Reactive
- **Effort Distribution**: 
  - Effort 1 (Simple): 30+ rules
  - Effort 2 (Moderate): 35+ rules  
  - Effort 3 (Complex): 20+ rules
  - Effort 4+ (Advanced): 5+ rules

## Duplicate Rules Removed

To ensure clean and maintainable rules, duplicate patterns have been consolidated:
- **Core annotations** (`@RestController`, `@RequestMapping`, etc.) kept in `02-springboot-annotations-to-quarkus.yaml`
- **JPA/Data access** patterns kept in `05-springboot-data-to-quarkus.yaml`  
- **Testing patterns** kept in `06-springboot-testing-to-quarkus.yaml`
- **Security patterns** kept in `07-springboot-security-to-quarkus.yaml`
- **Book-specific rules** focus on unique patterns not covered in core rules

## Additional Resources

Based on the [Quarkus for Spring Developers eBook examples](https://github.com/quarkus-for-spring-developers/examples):

- [Quarkus for Spring Developers eBook](https://red.ht/quarkus-spring-devs)
- [Chapter Examples Repository](https://github.com/quarkus-for-spring-developers/examples)
- [Quarkus Migration Guide from Spring Boot](https://quarkus.io/guides/spring-boot-properties)
- [Quarkus Spring Compatibility Extensions](https://quarkus.io/guides/spring-di)
- [Quarkus Getting Started Guide](https://quarkus.io/get-started/)
- [Quarkus Configuration Guide](https://quarkus.io/guides/config)

This comprehensive ruleset provides guidance for migrating from Spring Boot to Quarkus while maintaining functionality and improving performance characteristics. The rules are based on real-world patterns found in the official Quarkus for Spring Developers examples repository.
