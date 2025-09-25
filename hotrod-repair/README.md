# 🏁 Hot Rods Car Repair - JBoss EAP 7 Migration Example

This module demonstrates a comprehensive **JBoss EAP 7** application that uses deprecated APIs and patterns requiring migration to **EAP 8**. The application simulates a hot rods car repair shop management system and is specifically designed to trigger static code analysis tools to identify migration issues.

## 🎯 **Application Overview**

**Hot Rods Car Repair Shop** is a full-featured Java EE 7 web application that manages:
- 👥 **Customer Management** - Customer registration, profiles, and contact information
- 🚗 **Vehicle Registry** - Classic cars, hot rods, and performance vehicle tracking  
- 🔧 **Service Orders** - Repair jobs, parts, labor, and service history
- 📊 **Reporting** - Business analytics and revenue tracking

## 🚨 **Deprecated Features Demonstrated**

Based on the [Konveyor EAP 8 rulesets](https://github.com/konveyor/rulesets/tree/main/default/generated/eap8), this application includes:

### **1. Namespace Changes (EAP 8)**
- ✅ `javax.persistence` → **jakarta.persistence**
- ✅ `javax.ejb` → **jakarta.ejb** 
- ✅ `javax.servlet` → **jakarta.servlet**
- ✅ `javax.validation` → **jakarta.validation**

### **2. JPA 2.1 Deprecated Patterns**
- ✅ **@Temporal annotation** usage patterns
- ✅ **Legacy entity relationship** mappings
- ✅ **Deprecated named queries** and JPQL patterns
- ✅ **Manual flush()** operations
- ✅ **Legacy cascade** and fetch strategies

### **3. EJB 3.2 Deprecated Patterns**
- ✅ **@Local/@Remote** interface separation
- ✅ **Legacy transaction** attribute patterns
- ✅ **Deprecated EJB injection** patterns
- ✅ **Manual EntityManager** operations

### **4. Servlet 3.1 Deprecated Patterns**
- ✅ **Legacy servlet** patterns and response handling
- ✅ **Deprecated HTML generation** in servlets
- ✅ **Manual parameter** extraction patterns
- ✅ **Session-based** state management

### **5. JSP Deprecated Patterns**
- ✅ **JSP scriptlets** with business logic
- ✅ **Deprecated JNDI lookups** in JSP
- ✅ **Inline JavaScript** in JSP pages
- ✅ **Manual error handling** in presentation layer

### **6. Configuration Deprecated Patterns**
- ✅ **Legacy web.xml** configuration patterns
- ✅ **JPA 2.1 persistence.xml** settings
- ✅ **Deprecated Hibernate** properties
- ✅ **Legacy security** constraint definitions

## 🏗️ **Architecture**

```
hotrods-car-repair/
├── src/main/java/com/redhat/hotrods/repair/
│   ├── entity/           # JPA entities with deprecated patterns
│   │   ├── Customer.java
│   │   ├── Vehicle.java
│   │   ├── ServiceOrder.java
│   │   └── ServiceItem.java
│   ├── service/          # EJB session beans with deprecated APIs
│   │   ├── CustomerService.java
│   │   ├── CustomerServiceLocal.java
│   │   ├── CustomerServiceRemote.java
│   │   └── RepairService.java
│   └── web/              # Servlets with deprecated patterns
│       └── CustomerServlet.java
├── src/main/webapp/      # JSPs and web resources
│   ├── index.jsp         # Main page with JSP scriptlets
│   ├── customer-form.jsp # Form with deprecated patterns
│   └── WEB-INF/
│       └── web.xml       # Legacy servlet configuration
├── src/main/resources/META-INF/
│   └── persistence.xml   # JPA 2.1 configuration
├── database/             # Database setup
│   └── init-scripts/     # PostgreSQL initialization
├── docker-compose.yml    # Database and app server setup
└── pom.xml              # EAP 7 dependencies
```

## 🚀 **Quick Start**

### **Prerequisites**
- Java 8+
- Maven 3.6+
- Docker and Docker Compose
- JBoss EAP 7 or WildFly 20+ (optional)

### **1. Start Database Services**

```bash
cd hotrods-car-repair
docker-compose up -d postgres pgadmin
```

This starts:
- **PostgreSQL** database on port 5433
- **pgAdmin** web interface on port 5051

### **2. Build the Application**

```bash
# From project root
mvn clean package

# Build WAR file
cd hotrods-car-repair
mvn clean package
```

### **3. Deploy to JBoss EAP 7**

**Option A: Manual Deployment**
```bash
# Copy WAR to EAP deployments directory
cp target/hotrods-car-repair.war $EAP_HOME/standalone/deployments/
```

**Option B: Docker Deployment**
```bash
# Start full stack including EAP
docker-compose up -d
```

### **4. Access the Application**

- **Application**: http://localhost:8080/hotrods-car-repair
- **Customer Management**: http://localhost:8080/hotrods-car-repair/customers
- **pgAdmin**: http://localhost:5051 (admin@hotrods.com / admin123)

## 🗄️ **Database Access**

- **PostgreSQL**: localhost:5433
  - Database: `hotrods_repair`
  - Schema: `hotrods`
  - Username: `hotrods`
  - Password: `hotrods123`

- **pgAdmin**: http://localhost:5051
  - Email: `admin@hotrods.com`
  - Password: `admin123`

## 🧪 **Sample Data**

The database includes realistic sample data:
- **8 customers** with hot rod enthusiast profiles
- **11 classic vehicles** (Corvettes, Mustangs, Camaros, etc.)
- **10+ service orders** with various statuses
- **Service items** including parts, labor, and services

## 🔍 **Migration Analysis**

This application is specifically designed for **static code analysis tools** to detect:

1. **Namespace migrations** from javax to jakarta
2. **JPA deprecated patterns** requiring updates
3. **EJB anti-patterns** and legacy configurations
4. **Servlet deprecated methods** and patterns
5. **JSP scriptlet usage** needing modernization
6. **Configuration file** deprecated settings

### **Expected Static Analysis Findings:**

- **High Priority**: Namespace changes (javax → jakarta)
- **Medium Priority**: JPA 2.1 → JPA 3.0+ patterns
- **Medium Priority**: EJB 3.2 → EJB 4.0+ patterns  
- **Low Priority**: JSP scriptlet modernization
- **Low Priority**: Configuration file updates

## 🛠️ **Development**

### **Hot Reload Development**
```bash
# Terminal 1: Start database
docker-compose up postgres

# Terminal 2: Deploy to local EAP
mvn clean package wildfly:deploy

# Make changes and redeploy
mvn compile wildfly:deploy
```

### **Testing**
```bash
# Run unit tests
mvn test

# Integration tests with Arquillian
mvn verify
```

## 🔧 **Migration Path to EAP 8**

When migrating this application to EAP 8:

1. **Update namespaces**: javax → jakarta in all imports
2. **Upgrade JPA**: Update persistence.xml to JPA 3.0+
3. **Modernize EJB**: Remove @Local/@Remote separation
4. **Update Servlets**: Use modern servlet patterns
5. **Replace JSP scriptlets**: Use modern templating
6. **Update configuration**: Migrate web.xml patterns

## 📊 **Business Context**

This application represents a **typical enterprise Java EE 7 application** that many organizations need to migrate to modern platforms. The hot rods car repair shop context provides:

- **Realistic business logic** with complex entity relationships
- **Production-like patterns** found in real enterprise applications  
- **Comprehensive coverage** of deprecated Java EE features
- **Authentic data models** representing actual business needs

Perfect for testing migration tools, static analyzers, and modernization platforms!

---

*This application demonstrates deprecated JBoss EAP 7 patterns for educational and migration analysis purposes.*
