# Migration Specification: OSGi/Karaf to Spring Boot

## Overview

This document provides a comprehensive migration guide for migrating Apache Camel applications from OSGi Blueprint/Karaf to Spring Boot.

### Source Stack
- **Container**: OSGi Blueprint
- **Apache Camel**: 2.23.2
- **Apache Karaf**: 4.2.15
- **RedHat Fuse**: 7.11
- **JDK**: 8
- **Messaging**: WebSphere MQ via custom WmqComponent
- **Database**: Oracle Database via OSGi DataSource reference
- **JMS**: WebLogic JMS

### Target Stack
- **Container**: Spring Boot 3.2.0
- **Apache Camel**: 4.4.0
- **JDK**: 17
- **Messaging**: IBM MQ Client with Camel JMS component
- **Database**: Oracle JDBC with Spring JDBC
- **JMS**: WebLogic JMS
- **Libraries**: SWIFT SIL libraries (2.12.0.1) via system scope

### Migration Complexity
- **Estimated Effort**: High (8-12 weeks for medium-sized application)
- **Risk Level**: High
- **Compatibility Breaking Changes**: Significant

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Core Framework Migration](#core-framework-migration)
3. [Dependency Injection Migration](#dependency-injection-migration)
4. [Camel Routes Migration](#camel-routes-migration)
5. [Database Integration Migration](#database-integration-migration)
6. [Messaging Integration Migration](#messaging-integration-migration)
7. [Configuration Management](#configuration-management)
8. [Unit Testing Migration](#unit-testing-migration)
9. [Phased Migration Plan](#phased-migration-plan)
10. [Common Patterns and Anti-Patterns](#common-patterns-and-anti-patterns)

---

## 1. Prerequisites

### Development Environment Setup

#### JDK Installation
```bash
# Remove JDK 8 references
# Install JDK 17
sdk install java 17.0.8-tem
sdk use java 17.0.8-tem

# Verify installation
java -version
# Expected: openjdk version "17.0.8" or later
```

#### Maven Configuration
Update `~/.m2/settings.xml` to include IBM MQ and Oracle repositories:

```xml
<settings>
  <profiles>
    <profile>
      <id>spring-boot-migration</id>
      <repositories>
        <repository>
          <id>ibm-mq-repo</id>
          <url>https://repo1.maven.org/maven2/</url>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>spring-boot-migration</activeProfile>
  </activeProfiles>
</settings>
```

---

## 2. Core Framework Migration

### 2.1 Project Structure

#### Before (OSGi/Karaf)
```
my-karaf-project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── routes/
│   │   │       ├── processors/
│   │   │       └── beans/
│   │   └── resources/
│   │       ├── OSGI-INF/
│   │       │   └── blueprint/
│   │       │       └── camel-context.xml
│   │       └── features.xml
│   └── test/
│       └── java/
└── features/
```

#### After (Spring Boot)
```
my-springboot-project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── Application.java
│   │   │       ├── config/
│   │   │       ├── routes/
│   │   │       ├── processors/
│   │   │       └── services/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
│       ├── java/
│       └── resources/
│           └── application-test.yml
```

### 2.2 POM.xml Migration

#### Before (OSGi/Karaf pom.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>karaf-camel-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>bundle</packaging>

    <properties>
        <java.version>1.8</java.version>
        <camel.version>2.23.2</camel.version>
        <karaf.version>4.2.15</karaf.version>
    </properties>

    <dependencies>
        <!-- Camel Core -->
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-core</artifactId>
            <version>${camel.version}</version>
        </dependency>

        <!-- Camel Blueprint -->
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-blueprint</artifactId>
            <version>${camel.version}</version>
        </dependency>

        <!-- OSGi -->
        <dependency>
            <groupId>org.osgi</groupId>
            <artifactId>org.osgi.core</artifactId>
            <version>6.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <version>4.2.1</version>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <Bundle-SymbolicName>${project.artifactId}</Bundle-SymbolicName>
                        <Import-Package>
                            org.apache.camel*;version="[2.23,3)",
                            *
                        </Import-Package>
                    </instructions>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### After (Spring Boot pom.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>springboot-camel-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
        <camel.version>4.4.0</camel.version>
        <swift-sil.version>2.12.0.1</swift-sil.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <!-- Camel Spring Boot Starter -->
        <dependency>
            <groupId>org.apache.camel.springboot</groupId>
            <artifactId>camel-spring-boot-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>

        <!-- Camel Core (automatically managed) -->
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-core</artifactId>
            <version>${camel.version}</version>
        </dependency>

        <!-- Spring Boot Actuator for monitoring -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- SWIFT SIL Libraries -->
        <dependency>
            <groupId>com.swift.sil</groupId>
            <artifactId>swift-sil</artifactId>
            <version>${swift-sil.version}</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/swift-sil-${swift-sil.version}.jar</systemPath>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <includeSystemScope>true</includeSystemScope>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Key Changes:**
1. **Packaging**: `bundle` → `jar`
2. **Parent POM**: Added Spring Boot parent
3. **Java Version**: `1.8` → `17`
4. **Dependencies**: Replaced `camel-blueprint` with `camel-spring-boot-starter`
5. **Build Plugin**: Replaced `maven-bundle-plugin` with `spring-boot-maven-plugin`
6. **System Dependencies**: Added configuration to include system-scoped SWIFT libraries

---

## 3. Dependency Injection Migration

### 3.1 Blueprint XML to Spring Boot Configuration

#### Before (Blueprint XML - OSGI-INF/blueprint/camel-context.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:cm="http://aries.apache.org/blueprint/xmlns/blueprint-cm/v1.1.0"
           xsi:schemaLocation="
               http://www.osgi.org/xmlns/blueprint/v1.0.0
               https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd">

    <!-- Property placeholder -->
    <cm:property-placeholder persistent-id="com.example.app" update-strategy="reload">
        <cm:default-properties>
            <cm:property name="broker.url" value="tcp://localhost:61616"/>
            <cm:property name="db.url" value="jdbc:oracle:thin:@localhost:1521:orcl"/>
        </cm:default-properties>
    </cm:property-placeholder>

    <!-- DataSource reference from OSGi registry -->
    <reference id="dataSource" interface="javax.sql.DataSource"
               filter="(osgi.jndi.service.name=jdbc/oracleDS)"/>

    <!-- Bean definitions -->
    <bean id="orderProcessor" class="com.example.processors.OrderProcessor">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- Camel Context -->
    <camelContext id="camelContext" xmlns="http://camel.apache.org/schema/blueprint">
        <route id="orderRoute">
            <from uri="wmq:queue:ORDERS.IN"/>
            <to uri="bean:orderProcessor"/>
            <to uri="wmq:queue:ORDERS.OUT"/>
        </route>
    </camelContext>

</blueprint>
```

#### After (Spring Boot Java Configuration)

**Application.java**
```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**DatabaseConfig.java**
```java
package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("oracle.jdbc.OracleDriver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

**OrderProcessor.java**
```java
package com.example.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class OrderProcessor {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderProcessor(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void process(String order) {
        // Processing logic
        jdbcTemplate.update("INSERT INTO orders (order_data) VALUES (?)", order);
    }
}
```

**application.yml**
```yaml
spring:
  application:
    name: camel-springboot-app

  datasource:
    url: jdbc:oracle:thin:@localhost:1521:orcl
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD:admin}
    driver-class-name: oracle.jdbc.OracleDriver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000

camel:
  springboot:
    name: camelContext
    main-run-controller: true
```

**Key Changes:**
1. **Configuration**: Blueprint XML → Spring Java Configuration (`@Configuration`)
2. **Dependency Injection**: Blueprint `<bean>` → Spring `@Component` / `@Bean`
3. **Properties**: `cm:property-placeholder` → `application.yml` with `@Value`
4. **DataSource**: OSGi reference → Spring Boot auto-configuration with HikariCP
5. **Camel Context**: Blueprint XML routes → Java DSL routes (see section 4)

---

## 4. Camel Routes Migration

### 4.1 Route Definition Migration

#### Before (Camel 2.23.2 Blueprint XML)
```xml
<camelContext id="camelContext" xmlns="http://camel.apache.org/schema/blueprint">

    <!-- Simple route -->
    <route id="orderRoute">
        <from uri="wmq:queue:ORDERS.IN"/>
        <log message="Received order: ${body}"/>
        <to uri="bean:orderProcessor?method=process"/>
        <to uri="wmq:queue:ORDERS.OUT"/>
    </route>

    <!-- Content-based routing -->
    <route id="contentBasedRoute">
        <from uri="wmq:queue:PAYMENTS.IN"/>
        <choice>
            <when>
                <xpath>/payment/amount &gt; 1000</xpath>
                <to uri="wmq:queue:HIGH.VALUE"/>
            </when>
            <otherwise>
                <to uri="wmq:queue:NORMAL.VALUE"/>
            </otherwise>
        </choice>
    </route>

    <!-- Aggregation -->
    <route id="aggregationRoute">
        <from uri="wmq:queue:BATCH.IN"/>
        <aggregate strategyRef="myAggregationStrategy" completionSize="10" completionTimeout="5000">
            <correlationExpression>
                <header>batchId</header>
            </correlationExpression>
            <to uri="bean:batchProcessor"/>
        </aggregate>
    </route>

</camelContext>

<bean id="myAggregationStrategy" class="com.example.strategies.MyAggregationStrategy"/>
```

#### After (Camel 4.4.0 Spring Boot Java DSL)

**OrderRoute.java**
```java
package com.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Simple route
        from("jms:queue:ORDERS.IN")
            .routeId("orderRoute")
            .log("Received order: ${body}")
            .bean("orderProcessor", "process")
            .to("jms:queue:ORDERS.OUT");
    }
}
```

**ContentBasedRoute.java**
```java
package com.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContentBasedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Content-based routing
        from("jms:queue:PAYMENTS.IN")
            .routeId("contentBasedRoute")
            .choice()
                .when(xpath("/payment/amount > 1000"))
                    .to("jms:queue:HIGH.VALUE")
                .otherwise()
                    .to("jms:queue:NORMAL.VALUE")
            .end();
    }
}
```

**AggregationRoute.java**
```java
package com.example.routes;

import com.example.strategies.MyAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AggregationRoute extends RouteBuilder {

    @Autowired
    private MyAggregationStrategy myAggregationStrategy;

    @Override
    public void configure() throws Exception {

        // Aggregation
        from("jms:queue:BATCH.IN")
            .routeId("aggregationRoute")
            .aggregate(header("batchId"), myAggregationStrategy)
                .completionSize(10)
                .completionTimeout(5000)
            .to("bean:batchProcessor");
    }
}
```

**MyAggregationStrategy.java**
```java
package com.example.strategies;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class MyAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        String mergedBody = oldBody + "," + newBody;

        oldExchange.getIn().setBody(mergedBody);
        return oldExchange;
    }
}
```

**Key Changes:**
1. **Route Format**: XML → Java DSL
2. **Component Annotation**: Routes are Spring `@Component` beans
3. **URI Scheme**: `wmq:` → `jms:` (with proper JMS configuration)
4. **Dependency Injection**: Constructor/field injection for strategies
5. **Route Builder**: Each route class extends `RouteBuilder`

### 4.2 Camel 2.x to 4.x API Changes

#### Before (Camel 2.23.2)
```java
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class LegacyProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // Camel 2.x - Using getIn() and getOut()
        String body = exchange.getIn().getBody(String.class);
        String header = exchange.getIn().getHeader("myHeader", String.class);

        // Process
        String processed = body.toUpperCase();

        // Set output
        exchange.getOut().setBody(processed);
        exchange.getOut().setHeader("processed", true);
    }
}
```

#### After (Camel 4.4.0)
```java
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class ModernProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // Camel 4.x - Using getMessage() (getOut() is deprecated)
        String body = exchange.getMessage().getBody(String.class);
        String header = exchange.getMessage().getHeader("myHeader", String.class);

        // Process
        String processed = body.toUpperCase();

        // Set output - directly on getMessage()
        exchange.getMessage().setBody(processed);
        exchange.getMessage().setHeader("processed", true);
    }
}
```

**Key API Changes:**
1. **Message Access**: `getIn()`/`getOut()` → `getMessage()`
2. **Type Conversion**: Improved type converter registry
3. **Header Types**: Stronger typing in Camel 4
4. **Exception Handling**: Enhanced exception handling mechanisms

---

## 5. Database Integration Migration

### 5.1 OSGi DataSource to Spring JDBC

#### Before (OSGi Blueprint)
```xml
<!-- Blueprint XML -->
<reference id="dataSource" interface="javax.sql.DataSource"
           filter="(osgi.jndi.service.name=jdbc/oracleDS)"/>

<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
    <property name="dataSource" ref="dataSource"/>
</bean>

<!-- Camel SQL Component -->
<to uri="sql:SELECT * FROM orders WHERE status = :#status?dataSource=#dataSource"/>
```

**Java Usage (OSGi)**
```java
package com.example.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class OrderDao {

    private JdbcTemplate jdbcTemplate;

    // Blueprint injection
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> findOrders(String status) {
        return jdbcTemplate.query(
            "SELECT * FROM orders WHERE status = ?",
            new Object[]{status},
            new OrderRowMapper()
        );
    }
}
```

#### After (Spring Boot)

**application.yml**
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@${DB_HOST:localhost}:${DB_PORT:1521}:${DB_SID:orcl}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: oracle.jdbc.OracleDriver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: OracleHikariPool

  jpa:
    database-platform: org.hibernate.dialect.Oracle12cDialect
    show-sql: false
    hibernate:
      ddl-auto: validate
```

**DatabaseConfig.java**
```java
package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

**OrderDao.java**
```java
package com.example.dao;

import com.example.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> findOrders(String status) {
        return jdbcTemplate.query(
            "SELECT * FROM orders WHERE status = ?",
            new OrderRowMapper(),
            status
        );
    }

    @Transactional
    public void saveOrder(Order order) {
        jdbcTemplate.update(
            "INSERT INTO orders (id, customer_id, status, total) VALUES (?, ?, ?, ?)",
            order.getId(),
            order.getCustomerId(),
            order.getStatus(),
            order.getTotal()
        );
    }

    private static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setCustomerId(rs.getLong("customer_id"));
            order.setStatus(rs.getString("status"));
            order.setTotal(rs.getBigDecimal("total"));
            return order;
        }
    }
}
```

**Camel SQL Component Usage**
```java
package com.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer:dbQuery?period=60000")
            .setHeader("status", constant("PENDING"))
            .to("sql:SELECT * FROM orders WHERE status = :#status?dataSource=#dataSource")
            .log("Found ${body.size()} pending orders")
            .split(body())
                .to("direct:processOrder");
    }
}
```

**Key Changes:**
1. **DataSource Management**: OSGi service reference → Spring Boot auto-configuration
2. **Connection Pooling**: Explicit HikariCP configuration
3. **Transaction Management**: `@EnableTransactionManagement` with `@Transactional`
4. **DAO Pattern**: Setter injection → Constructor injection
5. **Repository Annotation**: Use `@Repository` for Spring exception translation

---

## 6. Messaging Integration Migration

### 6.1 WebSphere MQ to IBM MQ with JMS

#### Before (Custom WmqComponent)

**Blueprint XML**
```xml
<blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">

    <!-- Custom WMQ Component -->
    <bean id="wmq" class="com.example.wmq.WmqComponent">
        <property name="queueManager" value="${wmq.queueManager}"/>
        <property name="hostname" value="${wmq.hostname}"/>
        <property name="port" value="${wmq.port}"/>
        <property name="channel" value="${wmq.channel}"/>
        <property name="username" value="${wmq.username}"/>
        <property name="password" value="${wmq.password}"/>
    </bean>

    <camelContext xmlns="http://camel.apache.org/schema/blueprint">
        <route>
            <from uri="wmq:queue:ORDERS.IN"/>
            <to uri="bean:orderProcessor"/>
            <to uri="wmq:queue:ORDERS.OUT"/>
        </route>
    </camelContext>

</blueprint>
```

**WmqComponent.java** (Custom Component)
```java
package com.example.wmq;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class WmqComponent extends DefaultComponent {

    private String queueManager;
    private String hostname;
    private int port;
    private String channel;

    // Custom implementation
    // ...
}
```

#### After (Standard IBM MQ with JMS Component)

**pom.xml dependencies**
```xml
<dependencies>
    <!-- IBM MQ Client -->
    <dependency>
        <groupId>com.ibm.mq</groupId>
        <artifactId>com.ibm.mq.allclient</artifactId>
        <version>9.3.4.1</version>
    </dependency>

    <!-- Camel JMS Component -->
    <dependency>
        <groupId>org.apache.camel.springboot</groupId>
        <artifactId>camel-jms-starter</artifactId>
        <version>4.4.0</version>
    </dependency>

    <!-- Spring JMS -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jms</artifactId>
    </dependency>

    <!-- Spring Boot Artemis (optional for embedded testing) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-artemis</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**IbmMqConfig.java**
```java
package com.example.config;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.ConnectionFactory;

@Configuration
public class IbmMqConfig {

    @Value("${ibm.mq.queue-manager}")
    private String queueManager;

    @Value("${ibm.mq.hostname}")
    private String hostname;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.username}")
    private String username;

    @Value("${ibm.mq.password}")
    private String password;

    @Bean
    public MQConnectionFactory mqConnectionFactory() throws Exception {
        MQConnectionFactory factory = new MQConnectionFactory();
        factory.setHostName(hostname);
        factory.setPort(port);
        factory.setQueueManager(queueManager);
        factory.setChannel(channel);
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        factory.setCCSID(1208); // UTF-8

        return factory;
    }

    @Bean
    public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
            MQConnectionFactory mqConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter adapter =
            new UserCredentialsConnectionFactoryAdapter();
        adapter.setTargetConnectionFactory(mqConnectionFactory);
        adapter.setUsername(username);
        adapter.setPassword(password);

        return adapter;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(
            UserCredentialsConnectionFactoryAdapter adapter) {
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory(adapter);
        cachingFactory.setSessionCacheSize(10);
        cachingFactory.setCacheProducers(true);
        cachingFactory.setCacheConsumers(true);

        return cachingFactory;
    }

    @Bean(name = "jms")
    public JmsComponent jmsComponent(ConnectionFactory cachingConnectionFactory) {
        JmsComponent component = new JmsComponent();
        component.setConnectionFactory(cachingConnectionFactory);
        component.setRequestTimeout(30000);
        component.setConcurrentConsumers(5);
        component.setMaxConcurrentConsumers(10);

        return component;
    }
}
```

**application.yml**
```yaml
ibm:
  mq:
    queue-manager: ${IBM_MQ_QUEUE_MANAGER:QM1}
    hostname: ${IBM_MQ_HOSTNAME:localhost}
    port: ${IBM_MQ_PORT:1414}
    channel: ${IBM_MQ_CHANNEL:DEV.APP.SVRCONN}
    username: ${IBM_MQ_USERNAME:app}
    password: ${IBM_MQ_PASSWORD:passw0rd}
```

**OrderRoute.java** (Updated)
```java
package com.example.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {

    @Autowired
    private JmsComponent jmsComponent;

    @Override
    public void configure() throws Exception {

        // Error handling
        errorHandler(deadLetterChannel("jms:queue:DLQ")
            .maximumRedeliveries(3)
            .redeliveryDelay(5000)
            .logStackTrace(true));

        // Main route
        from("jms:queue:ORDERS.IN")
            .routeId("orderRoute")
            .log("Received order from IBM MQ: ${body}")
            .bean("orderProcessor", "process")
            .to("jms:queue:ORDERS.OUT");

        // Request-reply pattern
        from("jms:queue:ORDER.REQUEST")
            .routeId("orderRequestRoute")
            .log("Order request: ${body}")
            .to("bean:orderService?method=processRequest")
            .log("Order response: ${body}");
    }
}
```

**Key Changes:**
1. **Component**: Custom `WmqComponent` → Standard `JmsComponent`
2. **Connection Factory**: Custom implementation → IBM MQ `MQConnectionFactory`
3. **Connection Pooling**: Added `CachingConnectionFactory`
4. **Security**: `UserCredentialsConnectionFactoryAdapter` for credentials
5. **URI Scheme**: `wmq:` → `jms:`
6. **Configuration**: Externalized to `application.yml`

### 6.2 WebLogic JMS Configuration

#### Before (OSGi JNDI Lookup)
```xml
<bean id="weblogicJndiTemplate" class="org.springframework.jndi.JndiTemplate">
    <property name="environment">
        <props>
            <prop key="java.naming.factory.initial">
                weblogic.jndi.WLInitialContextFactory
            </prop>
            <prop key="java.naming.provider.url">t3://localhost:7001</prop>
        </props>
    </property>
</bean>

<bean id="weblogicConnectionFactory"
      class="org.springframework.jndi.JndiObjectFactoryBean">
    <property name="jndiTemplate" ref="weblogicJndiTemplate"/>
    <property name="jndiName" value="jms/ConnectionFactory"/>
</bean>
```

#### After (Spring Boot)

**pom.xml**
```xml
<dependency>
    <groupId>com.oracle.weblogic</groupId>
    <artifactId>wlthint3client</artifactId>
    <version>12.2.1.4</version>
    <scope>system</scope>
    <systemPath>${weblogic.home}/wlserver/server/lib/wlthint3client.jar</systemPath>
</dependency>
```

**WebLogicJmsConfig.java**
```java
package com.example.config;

import org.apache.camel.component.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.naming.NamingException;
import java.util.Properties;

@Configuration
public class WebLogicJmsConfig {

    @Value("${weblogic.jndi.url}")
    private String jndiUrl;

    @Value("${weblogic.jndi.factory}")
    private String jndiFactory;

    @Bean
    public JndiTemplate weblogicJndiTemplate() {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", jndiFactory);
        props.setProperty("java.naming.provider.url", jndiUrl);

        JndiTemplate template = new JndiTemplate();
        template.setEnvironment(props);
        return template;
    }

    @Bean
    public JndiObjectFactoryBean weblogicConnectionFactory() {
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        factory.setJndiTemplate(weblogicJndiTemplate());
        factory.setJndiName("jms/ConnectionFactory");
        factory.setResourceRef(false);
        return factory;
    }

    @Bean
    public CachingConnectionFactory weblogicCachingConnectionFactory()
            throws NamingException {
        ConnectionFactory cf = (ConnectionFactory) weblogicConnectionFactory().getObject();
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory(cf);
        cachingFactory.setSessionCacheSize(10);
        return cachingFactory;
    }

    @Bean(name = "weblogicJms")
    public JmsComponent weblogicJmsComponent() throws NamingException {
        JmsComponent component = new JmsComponent();
        component.setConnectionFactory(weblogicCachingConnectionFactory());
        return component;
    }
}
```

**application.yml**
```yaml
weblogic:
  jndi:
    url: ${WEBLOGIC_JNDI_URL:t3://localhost:7001}
    factory: weblogic.jndi.WLInitialContextFactory
```

**Usage in Routes**
```java
from("weblogicJms:queue:WEBLOGIC.IN")
    .to("bean:processor")
    .to("weblogicJms:queue:WEBLOGIC.OUT");
```

---

## 7. Configuration Management

### 7.1 Property Management

#### Before (OSGi Config Admin)

**com.example.app.cfg** (etc/com.example.app.cfg)
```properties
broker.url=tcp://localhost:61616
db.url=jdbc:oracle:thin:@localhost:1521:orcl
db.username=admin
db.password=secret
```

**Blueprint XML**
```xml
<cm:property-placeholder persistent-id="com.example.app" update-strategy="reload">
    <cm:default-properties>
        <cm:property name="broker.url" value="tcp://localhost:61616"/>
    </cm:default-properties>
</cm:property-placeholder>
```

#### After (Spring Boot)

**application.yml**
```yaml
spring:
  application:
    name: camel-app
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

# Common properties
app:
  name: ${spring.application.name}
  version: @project.version@

# External configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
```

**application-dev.yml**
```yaml
ibm:
  mq:
    hostname: localhost
    port: 1414
    queue-manager: QM1
    channel: DEV.APP.SVRCONN
    username: app
    password: passw0rd

spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:orcl
    username: dev_user
    password: dev_pass

logging:
  level:
    com.example: DEBUG
    org.apache.camel: INFO
```

**application-prod.yml**
```yaml
ibm:
  mq:
    hostname: ${IBM_MQ_HOST}
    port: ${IBM_MQ_PORT}
    queue-manager: ${IBM_MQ_QM}
    channel: ${IBM_MQ_CHANNEL}
    username: ${IBM_MQ_USER}
    password: ${IBM_MQ_PASSWORD}

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

logging:
  level:
    com.example: INFO
    org.apache.camel: WARN
```

**Using @ConfigurationProperties**
```java
package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Component
@ConfigurationProperties(prefix = "ibm.mq")
@Validated
public class IbmMqProperties {

    @NotBlank
    private String hostname;

    @Positive
    private int port;

    @NotBlank
    private String queueManager;

    @NotBlank
    private String channel;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Getters and setters
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    // ... other getters/setters
}
```

**Usage**
```java
@Component
public class SomeService {

    private final IbmMqProperties mqProperties;

    public SomeService(IbmMqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    public void connect() {
        String host = mqProperties.getHostname();
        // ...
    }
}
```

**Key Changes:**
1. **Configuration Format**: `.cfg` files → `application.yml`
2. **Profile Support**: Environment-specific configurations via Spring Profiles
3. **Type Safety**: `@ConfigurationProperties` for type-safe configuration
4. **Validation**: Built-in validation with Jakarta Validation
5. **Externalization**: Environment variables and system properties support

---

## 8. Unit Testing Migration

### 8.1 Test Framework Migration

#### Before (Camel 2.x with OSGi)

**pom.xml**
```xml
<dependencies>
    <dependency>
        <groupId>org.apache.camel</groupId>
        <artifactId>camel-test</artifactId>
        <version>2.23.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.camel</groupId>
        <artifactId>camel-test-blueprint</artifactId>
        <version>2.23.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**OrderRouteTest.java**
```java
package com.example.routes;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class OrderRouteTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/camel-context.xml";
    }

    @Test
    public void testOrderRoute() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);

        template.sendBody("wmq:queue:ORDERS.IN", "test order");

        assertMockEndpointsSatisfied();
    }
}
```

#### After (Camel 4.x with Spring Boot)

**pom.xml**
```xml
<dependencies>
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Camel Spring Boot Test -->
    <dependency>
        <groupId>org.apache.camel</groupId>
        <artifactId>camel-test-spring-junit5</artifactId>
        <version>4.4.0</version>
        <scope>test</scope>
    </dependency>

    <!-- Embedded JMS for testing -->
    <dependency>
        <groupId>org.apache.activemq</groupId>
        <artifactId>artemis-jakarta-server</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**OrderRouteTest.java**
```java
package com.example.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:result")
    private MockEndpoint mockResult;

    @Test
    public void testOrderRoute() throws Exception {
        // Expectations
        mockResult.expectedMessageCount(1);
        mockResult.expectedBodiesReceived("test order");

        // Send message
        producerTemplate.sendBody("jms:queue:ORDERS.IN", "test order");

        // Assertions
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testOrderWithHeader() throws Exception {
        mockResult.expectedMessageCount(1);
        mockResult.expectedHeaderReceived("orderType", "STANDARD");

        producerTemplate.sendBodyAndHeader(
            "jms:queue:ORDERS.IN",
            "test order",
            "orderType",
            "STANDARD"
        );

        mockResult.assertIsSatisfied();
    }
}
```

**application-test.yml**
```yaml
spring:
  artemis:
    mode: embedded

  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

camel:
  springboot:
    name: test-camel-context

# Override JMS to use embedded Artemis
ibm:
  mq:
    enabled: false
```

**Test Configuration**
```java
package com.example.config;

import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("test-data.sql")
            .build();
    }
}
```

**Advanced Route Testing with AdviceWith**
```java
package com.example.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRouteAdvancedTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:jms-out")
    private MockEndpoint mockJmsOut;

    @Test
    public void testOrderRouteWithMocking() throws Exception {
        // Modify route to replace JMS endpoint with mock
        AdviceWith.adviceWith(camelContext, "orderRoute", route -> {
            route.replaceFromWith("direct:start");
            route.weaveByToUri("jms:queue:ORDERS.OUT").replace().to("mock:jms-out");
        });

        camelContext.start();

        // Expectations
        mockJmsOut.expectedMessageCount(1);
        mockJmsOut.expectedBodiesReceived("PROCESSED: test order");

        // Send message
        producerTemplate.sendBody("direct:start", "test order");

        // Assertions
        mockJmsOut.assertIsSatisfied();
    }
}
```

**Integration Test with Testcontainers**
```java
package com.example.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@CamelSpringBootTest
@SpringBootTest
@Testcontainers
public class DatabaseIntegrationTest {

    @Container
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    @Container
    static GenericContainer<?> ibmMq = new GenericContainer<>(
            DockerImageName.parse("ibmcom/mq:latest"))
        .withEnv("LICENSE", "accept")
        .withEnv("MQ_QMGR_NAME", "QM1")
        .withExposedPorts(1414);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);

        registry.add("ibm.mq.hostname", ibmMq::getHost);
        registry.add("ibm.mq.port", () -> ibmMq.getMappedPort(1414));
    }

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    public void testEndToEndIntegration() throws Exception {
        // Integration test logic
        producerTemplate.sendBody("direct:start", "integration test");

        // Verify database state, JMS messages, etc.
    }
}
```

**Key Changes:**
1. **Test Framework**: JUnit 4 → JUnit 5
2. **Camel Test Support**: `CamelBlueprintTestSupport` → `@CamelSpringBootTest`
3. **Spring Boot Test**: Added `@SpringBootTest` for full context
4. **Mocking**: AdviceWith for route modification in tests
5. **Test Profiles**: Separate `application-test.yml`
6. **Integration Tests**: Testcontainers for real infrastructure
7. **Embedded Services**: Artemis for JMS, H2 for database

---

## 9. Phased Migration Plan

### Phase 1: Foundation (Weeks 1-2)

#### Objectives
- Set up Spring Boot project structure
- Migrate build configuration
- Establish CI/CD pipeline

#### Tasks

**Week 1: Project Setup**
1. Create new Spring Boot project structure
   ```bash
   mvn archetype:generate \
     -DarchetypeGroupId=org.apache.camel.archetypes \
     -DarchetypeArtifactId=camel-archetype-spring-boot \
     -DarchetypeVersion=4.4.0
   ```

2. Migrate `pom.xml`
   - Replace OSGi dependencies with Spring Boot
   - Add Camel Spring Boot starters
   - Configure Java 17 compiler
   - Add SWIFT SIL libraries as system dependencies

3. Create application structure
   ```
   src/main/java/com/example/
   ├── Application.java
   ├── config/
   ├── routes/ (placeholder)
   ├── processors/ (placeholder)
   └── services/ (placeholder)
   ```

4. Configure logging
   ```yaml
   # logback-spring.xml
   logging:
     pattern:
       console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
     level:
       com.example: DEBUG
       org.apache.camel: INFO
   ```

**Week 2: Core Configuration**
1. Create `application.yml` and profile-specific configs
2. Migrate property placeholders
3. Set up database configuration (without routes)
4. Configure IBM MQ connection (without routes)
5. Configure WebLogic JMS connection
6. Add health checks and actuator endpoints

**Deliverables:**
- Runnable Spring Boot application (no routes yet)
- All external connections configured
- Health endpoints responding
- Unit test structure in place

**Testing:**
```bash
# Verify application starts
mvn spring-boot:run

# Check health endpoint
curl http://localhost:8080/actuator/health

# Verify database connection
# Verify IBM MQ connection (via admin console)
```

---

### Phase 2: Route Migration (Weeks 3-6)

#### Objectives
- Convert Blueprint XML routes to Java DSL
- Migrate custom components to standard components
- Implement error handling

#### Tasks

**Week 3: Simple Routes**
1. Identify and prioritize routes (start with simplest)
2. Convert 3-5 simple routes from Blueprint XML to Java DSL
3. Replace `wmq:` URIs with `jms:` URIs
4. Write unit tests for each route

**Route Complexity Assessment:**
```
Simple Routes (Week 3):
- Direct endpoint to endpoint
- Simple transformations
- No aggregation or splitting

Medium Routes (Week 4-5):
- Content-based routing
- Simple splitter/aggregator
- Bean method calls

Complex Routes (Week 6):
- Multi-step aggregation
- Dynamic routing
- Transaction management
```

**Week 4-5: Medium Complexity Routes**
1. Convert routes with content-based routing
2. Migrate routes with bean processors
3. Update aggregation strategies
4. Implement proper error handling
5. Add route-level tests

**Week 6: Complex Routes and Error Handling**
1. Convert remaining complex routes
2. Implement global error handling strategy
3. Add dead letter queue configuration
4. Configure retry policies
5. Add integration tests

**Code Example: Route Migration Checklist**
```java
@Component
public class MigratedRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // ✅ Error handling configured
        errorHandler(deadLetterChannel("jms:queue:DLQ")
            .maximumRedeliveries(3)
            .redeliveryDelay(5000));

        // ✅ Route ID specified
        from("jms:queue:INPUT")
            .routeId("migratedRoute")
            // ✅ Logging added
            .log("Processing message: ${body}")
            // ✅ Bean reference updated
            .bean("processor", "process")
            // ✅ URI updated from wmq: to jms:
            .to("jms:queue:OUTPUT");
    }
}
```

**Deliverables:**
- All routes converted to Java DSL
- Unit tests for all routes (80%+ coverage)
- Integration tests for critical paths
- Error handling strategy documented

---

### Phase 3: Data Access Migration (Weeks 7-8)

#### Objectives
- Migrate OSGi DataSource references to Spring JDBC
- Update DAO layer
- Migrate SQL queries and Camel SQL component usage

#### Tasks

**Week 7: Database Configuration**
1. Create `DatabaseConfig.java` with HikariCP
2. Migrate all JDBC templates
3. Update DAO classes to use constructor injection
4. Add transaction management
5. Create database integration tests

**Week 8: SQL Component and Queries**
1. Update Camel SQL component references
2. Migrate dynamic SQL queries
3. Add database health checks
4. Performance testing with connection pool
5. Add database unit tests with H2

**Migration Checklist:**
```
□ Replace OSGi DataSource reference with Spring DataSource
□ Add HikariCP configuration
□ Update all DAO classes to @Repository
□ Add @Transactional where needed
□ Replace setter injection with constructor injection
□ Add JdbcTemplate beans
□ Update Camel SQL component dataSource references
□ Create test data scripts (schema.sql, data.sql)
□ Add database integration tests
□ Configure connection pool monitoring
```

**Deliverables:**
- All database access migrated to Spring JDBC
- Transaction management in place
- DAO unit tests (90%+ coverage)
- Database integration tests
- Performance benchmarks documented

---

### Phase 4: Unit Test Migration (Week 9)

#### Objectives
- Migrate all unit tests to JUnit 5
- Update Camel test framework usage
- Achieve same or better test coverage

#### Tasks

**Day 1-2: Test Framework Setup**
1. Add Camel Spring Boot test dependencies
2. Create test configuration classes
3. Set up embedded services (Artemis, H2)
4. Create `application-test.yml`

**Day 3-4: Route Test Migration**
1. Convert route tests to `@CamelSpringBootTest`
2. Update mock endpoint usage
3. Add AdviceWith for endpoint mocking
4. Verify test coverage maintained

**Day 5: Integration Tests**
1. Create integration test suite
2. Add Testcontainers for Oracle and IBM MQ
3. Create end-to-end test scenarios
4. Document test strategy

**Test Migration Pattern:**
```java
// Before (Camel 2.x Blueprint)
public class OldTest extends CamelBlueprintTestSupport {
    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/camel-context.xml";
    }

    @Test
    public void testRoute() throws Exception {
        // test logic
    }
}

// After (Camel 4.x Spring Boot)
@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("test")
public class NewTest {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate template;

    @Test
    public void testRoute() throws Exception {
        // test logic
    }
}
```

**Deliverables:**
- All tests migrated to JUnit 5
- Test coverage report (target: 80%+)
- Integration test suite
- Test documentation

---

### Phase 5: Integration and Performance Testing (Weeks 10-11)

#### Objectives
- End-to-end integration testing
- Performance benchmarking
- Load testing

#### Tasks

**Week 10: Integration Testing**
1. Deploy to integration environment
2. Run end-to-end test scenarios
3. Verify external system integrations
   - IBM MQ connectivity
   - Oracle database operations
   - WebLogic JMS
   - SWIFT SIL library integration
4. Security testing
5. Fix integration issues

**Integration Test Scenarios:**
```
Scenario 1: Order Processing Flow
1. Message arrives on IBM MQ queue
2. Route processes message
3. Database updated
4. Response sent to output queue
5. Verify all steps completed successfully

Scenario 2: Error Handling
1. Send invalid message
2. Verify error handling triggered
3. Check dead letter queue
4. Verify database rollback

Scenario 3: High Volume
1. Send 10,000 messages
2. Monitor processing rate
3. Verify no message loss
4. Check resource utilization
```

**Week 11: Performance Testing**
1. Establish baseline metrics from Karaf application
2. Run performance tests on Spring Boot application
3. Compare metrics:
   - Message throughput
   - Response times
   - Memory usage
   - CPU usage
4. Optimize configuration based on results
5. Document performance comparison

**Performance Test Script:**
```bash
#!/bin/bash
# Load test script

# JMeter or Gatling test
jmeter -n -t performance-test.jmx \
  -l results.jtl \
  -e -o reports/

# Analyze results
# - Throughput (messages/sec)
# - Average response time
# - Error rate
# - Resource utilization
```

**Deliverables:**
- Integration test report
- Performance test results
- Comparison with Karaf baseline
- Optimization recommendations
- Updated configuration for production

---

### Phase 6: Production Preparation (Week 12)

#### Objectives
- Production configuration
- Deployment automation
- Monitoring and alerting
- Documentation

#### Tasks

**Day 1-2: Production Configuration**
1. Create `application-prod.yml`
2. Configure production logging
3. Set up monitoring (Prometheus, Micrometer)
4. Configure health checks
5. Set up connection pool tuning
6. Security hardening

**Production Configuration Example:**
```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 20000

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: WARN
    com.example: INFO
  file:
    name: /var/log/app/application.log
    max-size: 100MB
    max-history: 30
```

**Day 3: Deployment Automation**
1. Create Dockerfile
2. Set up Kubernetes manifests (if applicable)
3. Configure CI/CD pipeline
4. Create deployment runbook

**Dockerfile:**
```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar
COPY lib/*.jar /app/lib/

ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Day 4: Monitoring Setup**
1. Configure application metrics
2. Set up dashboards (Grafana)
3. Configure alerting rules
4. Test monitoring pipeline

**Day 5: Documentation and Handover**
1. Update architecture documentation
2. Create operational runbook
3. Document troubleshooting procedures
4. Create rollback plan
5. Conduct knowledge transfer sessions

**Deliverables:**
- Production-ready configuration
- Deployment automation
- Monitoring dashboards
- Complete documentation
- Rollback procedures

---

### Phase 7: Deployment and Validation (Post Week 12)

#### Deployment Strategy

**Blue-Green Deployment:**
```
1. Deploy Spring Boot application (Green)
2. Run parallel with Karaf application (Blue)
3. Route 10% traffic to Green
4. Monitor for 24 hours
5. Gradually increase to 25%, 50%, 75%, 100%
6. Decommission Blue after 1 week of stable operation
```

**Rollback Triggers:**
- Error rate > 5%
- Response time degradation > 20%
- Message loss detected
- Critical functionality failure
- Performance degradation > 30%

**Validation Checklist:**
```
□ All routes processing messages
□ Database connections stable
□ IBM MQ connectivity confirmed
□ WebLogic JMS operational
□ Error handling working correctly
□ Metrics being collected
□ Alerts configured and tested
□ No memory leaks detected
□ Performance meets SLAs
□ All integration tests passing
```

---

## 10. Common Patterns and Anti-Patterns

### 10.1 Messaging Patterns

#### Pattern: Request-Reply

**Before (OSGi/Blueprint)**
```xml
<route>
    <from uri="wmq:queue:REQUEST"/>
    <inOut uri="bean:processor"/>
    <!-- Reply automatically sent to JMSReplyTo -->
</route>
```

**After (Spring Boot)**
```java
from("jms:queue:REQUEST")
    .bean("processor")
    .log("Reply: ${body}");
    // Reply handled by JMS component automatically
```

#### Pattern: Dead Letter Channel

**Before**
```xml
<errorHandler type="DeadLetterChannel" deadLetterUri="wmq:queue:DLQ">
    <redeliveryPolicy maximumRedeliveries="3" redeliveryDelay="5000"/>
</errorHandler>
```

**After**
```java
errorHandler(deadLetterChannel("jms:queue:DLQ")
    .maximumRedeliveries(3)
    .redeliveryDelay(5000)
    .retryAttemptedLogLevel(LoggingLevel.WARN)
    .useOriginalMessage());
```

### 10.2 Anti-Patterns to Avoid

#### ❌ Anti-Pattern 1: Using getOut() in Camel 4
```java
// DON'T DO THIS in Camel 4.x
exchange.getOut().setBody("result");  // Deprecated!

// DO THIS instead
exchange.getMessage().setBody("result");
```

#### ❌ Anti-Pattern 2: Blocking Calls in Routes
```java
// DON'T DO THIS
from("jms:queue:INPUT")
    .process(exchange -> {
        // Blocking call!
        Thread.sleep(10000);
    });

// DO THIS instead
from("jms:queue:INPUT")
    .threads(10)  // Use thread pool
    .process(exchange -> {
        // Process asynchronously
    });
```

#### ❌ Anti-Pattern 3: Hard-coded Configuration
```java
// DON'T DO THIS
MQConnectionFactory factory = new MQConnectionFactory();
factory.setHostName("prod-mq-server");  // Hard-coded!

// DO THIS instead
@Value("${ibm.mq.hostname}")
private String hostname;

factory.setHostName(hostname);
```

#### ❌ Anti-Pattern 4: Ignoring Connection Pooling
```java
// DON'T DO THIS
@Bean
public DataSource dataSource() {
    return new OracleDataSource();  // No pooling!
}

// DO THIS instead
@Bean
public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    // ... configure pooling
    return new HikariDataSource(config);
}
```

### 10.3 Best Practices

#### ✅ Use Constructor Injection
```java
@Component
public class OrderService {
    private final OrderDao orderDao;
    private final ProducerTemplate template;

    @Autowired  // Optional in Spring Boot
    public OrderService(OrderDao orderDao, ProducerTemplate template) {
        this.orderDao = orderDao;
        this.template = template;
    }
}
```

#### ✅ Externalize All Configuration
```yaml
# application.yml
app:
  order:
    max-retries: ${ORDER_MAX_RETRIES:3}
    timeout: ${ORDER_TIMEOUT:30000}
```

#### ✅ Use Type-Safe Configuration
```java
@ConfigurationProperties(prefix = "app.order")
@Validated
public class OrderProperties {
    @Min(1)
    private int maxRetries;

    @Positive
    private long timeout;
}
```

#### ✅ Implement Health Checks
```java
@Component
public class IbmMqHealthIndicator implements HealthIndicator {

    private final ConnectionFactory connectionFactory;

    @Override
    public Health health() {
        try (Connection conn = connectionFactory.createConnection()) {
            return Health.up()
                .withDetail("provider", "IBM MQ")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

---

## 11. Migration Checklist

### Pre-Migration
```
□ Inventory all Blueprint XML files
□ Document all OSGi service references
□ List all Camel components used
□ Identify custom components
□ Document database connections
□ List all messaging queues/topics
□ Review current monitoring setup
□ Backup source code and configuration
□ Establish baseline performance metrics
```

### During Migration
```
□ Set up Spring Boot project structure
□ Migrate pom.xml dependencies
□ Convert Blueprint XML to Java configuration
□ Migrate all Camel routes to Java DSL
□ Update WmqComponent to JMS component
□ Migrate database configuration
□ Update all unit tests
□ Add integration tests
□ Configure logging
□ Set up monitoring
□ Performance testing
□ Security review
```

### Post-Migration
```
□ Deploy to test environment
□ Run integration test suite
□ Performance comparison with baseline
□ Security scan
□ Load testing
□ Failover testing
□ Documentation updated
□ Team training completed
□ Deployment runbook created
□ Monitoring dashboards configured
□ Production deployment plan approved
```

---

## 12. Troubleshooting Guide

### Issue: Application fails to start

**Symptoms:**
```
Error creating bean with name 'dataSource'
```

**Solution:**
1. Check `application.yml` for correct database URL
2. Verify Oracle JDBC driver is in classpath
3. Check database credentials
4. Verify network connectivity to database

### Issue: JMS connection failures

**Symptoms:**
```
JMSWMQ2007: Failed to send a message to destination
```

**Solution:**
1. Verify IBM MQ queue manager is running
2. Check queue exists: `runmqsc QM1`
3. Verify channel permissions
4. Check firewall rules
5. Review IBM MQ logs

### Issue: Route not found

**Symptoms:**
```
No consumers available on endpoint
```

**Solution:**
1. Verify route is annotated with `@Component`
2. Check component scanning in `Application.java`
3. Verify route ID is unique
4. Check for route startup errors in logs

### Issue: Performance degradation

**Symptoms:**
- Slow message processing
- High memory usage
- CPU spikes

**Solution:**
1. Review connection pool configuration
2. Check for memory leaks (use VisualVM)
3. Analyze thread dumps
4. Review Camel route efficiency
5. Check database query performance
6. Review JVM heap settings

---

## 13. References

### Official Documentation
- [Apache Camel 4.x Migration Guide](https://camel.apache.org/manual/camel-4-migration-guide.html)
- [Spring Boot 3.2 Reference](https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/)
- [IBM MQ Spring Boot Starter](https://github.com/ibm-messaging/mq-spring-boot)
- [Oracle JDBC Documentation](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/)

### Tools
- [JDK 17 Download](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker](https://www.docker.com/)
- [Testcontainers](https://www.testcontainers.org/)

### Sample Code Repository
```bash
git clone https://github.com/example/karaf-to-springboot-migration.git
```

---

## Appendix A: Camel 2.x to 4.x Breaking Changes

### Component Renames
| Camel 2.x | Camel 4.x |
|-----------|-----------|
| camel-http4 | camel-http |
| camel-jetty9 | camel-jetty |
| camel-mongodb3 | camel-mongodb |

### API Changes
- `Exchange.getIn()` / `getOut()` → `Exchange.getMessage()`
- `@Converter` requires `generateLoader = true` parameter
- TypeConverter registry changes
- RouteBuilder lifecycle changes

### Removed Components
- camel-blueprint (use camel-spring-boot)
- camel-osgi (use Spring Boot alternatives)

---

## Appendix B: JDK 8 to 17 Migration Notes

### Language Changes
```java
// JDK 8
List<String> list = Arrays.asList("a", "b", "c");

// JDK 17 - Use newer APIs
List<String> list = List.of("a", "b", "c");
```

### Removed APIs
- `sun.*` packages
- Some deprecated Java EE packages

### Module System
Spring Boot handles modules automatically, but be aware of:
- JPMS (Java Platform Module System)
- Restricted reflection access
- Security manager deprecation

---

## Document Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-02-12 | Migration Team | Initial version |

---

**End of Migration Specification**
