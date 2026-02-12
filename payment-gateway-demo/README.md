# Payment Gateway Demo (Karaf / Camel 2 / Blueprint)

This is a **source-stack demo** for the migration described in `KARAF-TO-SPRINGBOOT-MIGRATION-SPEC.md`. It is intentionally built with the **pre-migration** stack so that Konveyor Kantra static analysis rules in `apache-karaf-camel4-migration.yaml` report findings.

## Source stack (as per spec)

| Item | Technology |
|------|------------|
| **Container** | OSGi Blueprint |
| **Apache Camel** | 2.23.2 |
| **Apache Karaf** | 4.2.15 |
| **Red Hat Fuse** | 7.11 |
| **JDK** | 8 |
| **Messaging** | WebSphere MQ via custom `WmqComponent` |
| **Database** | Oracle Database via OSGi DataSource reference |
| **JMS** | WebLogic JMS |

## Project layout

- **Blueprint**  
  - `src/main/resources/OSGI-INF/blueprint/camel-context.xml`  
  - Camel context, `wmq:` routes, `cm:property-placeholder`, OSGi `reference` to `javax.sql.DataSource`, and Blueprint `bean`s.

- **Custom WMQ component**  
  - `WmqComponent` extends `org.apache.camel.impl.DefaultComponent` (Kantra: replace with JmsComponent + IBM MQ).

- **Routes**  
  - Payment routes using `wmq:queue:PAYMENTS.IN`, `wmq:queue:PAYMENTS.OUT`, etc. (Kantra: replace with `jms:`).

- **Java**  
  - `PaymentProcessor` uses `exchange.getIn()` / `exchange.getOut()` (Camel 4: use `getMessage()`).  
  - `PaymentDao` uses setter `setJdbcTemplate` (Kantra: prefer constructor injection).  
  - `PaymentRouteBuilder` extends `RouteBuilder` and uses `wmq:` URIs.

- **Tests**  
  - `PaymentRouteBlueprintTest` extends `CamelBlueprintTestSupport`, uses `org.junit.Test` and `assertMockEndpointsSatisfied()` (Kantra: migrate to JUnit 5 and `assertIsSatisfied()`).

## Running Kantra

From the repo root (or the directory containing `apache-karaf-camel4-migration.yaml`):

```bash
kantra analyze --input payment-gateway-demo --output ./kantra-report --rules apache-karaf-camel4-migration.yaml
```

The report should list migration points for: packaging, Camel Blueprint → Spring Boot, maven-bundle-plugin, `getIn`/`getOut` → `getMessage`, Blueprint → Spring, OSGi DataSource → Spring DataSource, `wmq:` → `jms:`, `WmqComponent`/`DefaultComponent`, JUnit 4 → 5, `assertMockEndpointsSatisfied` → `assertIsSatisfied`, Java 8 → 17, Camel 2 → 4, and related items.

## Build and test

- **Compile:**  
  `mvn -f payment-gateway-demo clean compile`

- **Run tests:**  
  `mvn -f payment-gateway-demo test`  
  (Uses test Blueprint `camel-context-test.xml` with in-memory H2 and `direct:` endpoints.)

## Deployment (optional)

To run on Karaf 4.2.x: install the bundle and ensure an OSGi DataSource is registered with filter `(osgi.jndi.service.name=jdbc/oracleDS)`, and that WebSphere MQ (or a stub) is available if you exercise the `wmq:` routes.
