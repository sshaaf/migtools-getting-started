# Struts 1.x Bank Account Demo

A legacy **Apache Struts 1.x** bank account management application built for demonstrating modernization and migration scenarios.

## Overview

This application showcases classic Java EE patterns from the Struts 1.x era:
- **ActionServlet** as the front controller
- **ActionForm** for form data binding and validation
- **Action** classes for request handling
- **JSP** with Struts tag libraries for views
- **JDBC** for database operations

## Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Apache Struts 1.3.10 |
| View | JSP 2.3 + Struts Tag Libraries |
| Database | H2 (embedded, in-memory) |
| Build | Maven 3.x |
| Runtime | Java 8+, Servlet 3.1 Container |

## Features

- **Account Management**
  - List all bank accounts with summary statistics
  - View detailed account information
  - Create new accounts
  - Edit existing accounts
  - Delete (deactivate) accounts

- **Transactions**
  - Deposit funds
  - Withdraw funds (with balance validation)

## Project Structure

```
struts-bank-demo/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/redhat/bank/
        │       ├── action/           # Struts Action classes
        │       │   ├── ListAccountsAction.java
        │       │   ├── ViewAccountAction.java
        │       │   ├── EditAccountAction.java
        │       │   ├── SaveAccountAction.java
        │       │   ├── DeleteAccountAction.java
        │       │   └── TransactionAction.java
        │       ├── dao/              # Data Access Objects
        │       │   ├── AccountDAO.java
        │       │   └── DatabaseConnection.java
        │       ├── form/             # Struts ActionForms
        │       │   ├── AccountForm.java
        │       │   └── TransactionForm.java
        │       └── model/            # Domain models
        │           └── Account.java
        ├── resources/
        │   └── MessageResources.properties
        └── webapp/
            ├── index.jsp
            ├── list.jsp              # Account list view
            ├── view.jsp              # Account detail view
            ├── edit.jsp              # Account edit/create form
            ├── error.jsp             # Error page
            └── WEB-INF/
                ├── struts-config.xml # Struts configuration
                └── web.xml           # Deployment descriptor
```

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+
- A Servlet container (Tomcat 8.5+ recommended)

### Build

```bash
cd struts-bank-demo
mvn clean package
```

## Development Mode

Run the application locally with hot-reload capabilities for development and testing.

### Option 1: Tomcat 7 Embedded (Quick Start)

```bash
cd struts-2-springboot
mvn tomcat7:run
```

Then open: **http://localhost:8080/bank/**

Press `Ctrl+C` to stop the server.

### Option 2: Tomcat 9 via Cargo Plugin

For a more modern Tomcat 9 runtime:

```bash
mvn clean package cargo:run
```

Then open: **http://localhost:8080/bank/**

| Command | Description |
|---------|-------------|
| `mvn cargo:run` | Start Tomcat 9 with app deployed |
| `mvn cargo:start` | Start server in background |
| `mvn cargo:stop` | Stop background server |
| `mvn cargo:redeploy` | Redeploy without restart |

### Compatible Servers

Since this is a legacy `javax.*` namespace application, use servers that support Java EE 8:

| Server | Version | Notes |
|--------|---------|-------|
| Apache Tomcat | 8.5.x - 9.x | Recommended for development |
| WildFly | 18.x - 26.x | Last `javax.*` versions |
| JBoss EAP | 7.x | Enterprise supported |

> ⚠️ **Note:** WildFly 27+ and Tomcat 10+ use `jakarta.*` namespace and are **not compatible** with this application.

## Deploy to External Tomcat

1. Build the WAR file:
   ```bash
   mvn clean package
   ```

2. Copy `target/struts-bank-demo.war` to your Tomcat's `webapps/` directory

3. Access at: **http://localhost:8080/struts-bank-demo/**

## Sample Data

The application initializes with sample accounts on startup:

| Account Number | Holder | Type | Balance |
|----------------|--------|------|---------|
| ACC-001-2024 | John Smith | SAVINGS | $15,000.50 |
| ACC-002-2024 | Sarah Johnson | CHECKING | $8,750.25 |
| ACC-003-2024 | Tech Solutions Inc. | BUSINESS | $125,000.00 |
| ACC-004-2024 | Maria Garcia | SAVINGS | $3,200.75 |
| ACC-005-2024 | Robert Chen | CHECKING | $22,500.00 |

## Key Struts 1.x Patterns Demonstrated

### 1. ActionForm Validation
```java
public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    ActionErrors errors = new ActionErrors();
    if (holderName == null || holderName.trim().isEmpty()) {
        errors.add("holderName", new ActionMessage("error.holderName.required"));
    }
    return errors;
}
```

### 2. Action Execute Method
```java
public ActionForward execute(ActionMapping mapping, ActionForm form,
        HttpServletRequest request, HttpServletResponse response) throws Exception {
    List<Account> accounts = accountDAO.findAll();
    request.setAttribute("accounts", accounts);
    return mapping.findForward("success");
}
```

### 3. Struts Configuration (struts-config.xml)
```xml
<action path="/listAccounts"
        type="com.redhat.bank.action.ListAccountsAction"
        scope="request">
    <forward name="success" path="/list.jsp"/>
    <forward name="error" path="/error.jsp"/>
</action>
```

## Migration Rules

This project includes comprehensive **Kantra rules** for automated migration analysis:

```
rules/struts-to-springboot/
├── ruleset.yaml                              # Ruleset metadata
├── 00-struts-dependencies-to-springboot.yaml # Maven dependency migrations
├── 01-struts-action-to-springboot-controller.yaml # Action → @Controller
├── 02-struts-actionform-to-springboot-dto.yaml    # ActionForm → DTO + Bean Validation
├── 03-struts-config-to-springboot.yaml            # Configuration migrations
├── 04-struts-jsp-to-thymeleaf.yaml                # JSP → Thymeleaf templates
├── 05-struts-dao-to-springboot-repository.yaml    # JDBC DAO → Spring Data JPA
└── README.md                                      # Migration guide
```


## Why This Demo?

Struts 1.x reached end-of-life and has known security vulnerabilities. Many enterprises still run legacy Struts applications and need to modernize. This demo provides:

1. A realistic but simple Struts 1.x application
2. Classic enterprise Java patterns (DAO, Form validation, etc.)
3. A migration target for demonstrating tools like Konveyor
4. Educational value for understanding legacy Java EE patterns


