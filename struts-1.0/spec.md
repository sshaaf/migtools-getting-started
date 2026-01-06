# Struts to Spring Boot Migration Specification

## Purpose
This document defines the complete migration strategy for transforming Apache Struts 1.x applications into modern Spring Boot applications. It is designed for automated migration agents and provides generic, pattern-based transformation rules.

## Migration Philosophy

### Core Principles
1. **Preserve Business Logic**: Business logic embedded in Actions and DAOs must be preserved exactly
2. **Modernize Infrastructure**: Replace Struts framework code with Spring Boot equivalents
3. **Type Safety**: Convert string-based forms to properly typed DTOs
4. **Declarative Configuration**: Replace XML configuration with annotation-based configuration
5. **Dependency Injection**: Replace manual object creation with Spring's IoC container
6. **Production Readiness**: Introduce connection pooling, proper transaction management, and externalized configuration

### Migration Scope
- **Controller Layer**: Struts Actions → Spring MVC Controllers
- **Form Layer**: ActionForms → DTOs with Bean Validation
- **Data Access Layer**: JDBC DAOs → Spring Data JPA Repositories
- **View Layer**: JSP/Struts Tags → Thymeleaf (or REST APIs)
- **Configuration**: struts-config.xml + web.xml → Spring Boot auto-configuration
- **Dependency Management**: Struts libraries → Spring Boot starters

---

## Step 1: Project Structure and Build Configuration

### 1.1 Dependency Transformation

#### Pattern: Remove Struts Dependencies
**Detection**: Identify all Struts-related dependencies in build file
```xml
<!-- REMOVE these -->
<dependency>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts-core</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts-taglib</artifactId>
</dependency>
```

**Action**: Remove all dependencies matching:
- `groupId` contains `struts`
- `groupId` contains `org.apache.struts`

#### Pattern: Add Spring Boot Dependencies
**Action**: Add Spring Boot parent POM
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

**Action**: Add required Spring Boot starters based on detected patterns:

| Detected Pattern | Required Starter |
|-----------------|------------------|
| Classes extending `Action` | `spring-boot-starter-web` |
| Classes with JDBC code (`Connection`, `PreparedStatement`) | `spring-boot-starter-data-jpa` |
| JSP files in webapp directory | `spring-boot-starter-thymeleaf` |
| `ActionForm` with `validate()` methods | `spring-boot-starter-validation` |
| Database driver dependencies (H2, MySQL, etc.) | Keep existing + `spring-boot-starter-data-jpa` |

**Minimal Required Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### Pattern: Update Java Version
**Detection**: Check `maven.compiler.source` and `maven.compiler.target`
**Action**: If version < 17, update to Java 17 (Spring Boot 3.x requirement)
```xml
<properties>
    <java.version>17</java.version>
</properties>
```

#### Pattern: Update Packaging
**Current**: `<packaging>war</packaging>`
**Options**:
1. Keep WAR for traditional deployment
2. Change to `<packaging>jar</packaging>` for embedded Tomcat (recommended)

**Action**: If keeping WAR, add:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

### 1.2 Directory Structure Transformation

#### Pattern: Preserve Standard Maven Structure
**No Changes Required**:
- `src/main/java` - Keep as-is
- `src/main/resources` - Keep as-is
- `src/test/java` - Keep as-is

#### Pattern: Transform Webapp Directory
**Current Structure**:
```
src/main/webapp/
├── *.jsp
└── WEB-INF/
    ├── web.xml
    ├── struts-config.xml
    └── lib/
```

**Target Structure**:
```
src/main/resources/
├── templates/          # Thymeleaf templates (from JSPs)
├── static/            # CSS, JS, images
├── application.yml    # Spring Boot configuration
└── messages.properties # Keep existing i18n files
```

**Actions**:
1. Move JSP files to `src/main/resources/templates/` (will be converted in Step 5)
2. Extract CSS from JSP `<style>` blocks to `src/main/resources/static/css/`
3. Extract JavaScript to `src/main/resources/static/js/`
4. Delete `WEB-INF/web.xml` (replaced by Spring Boot auto-config)
5. Archive `WEB-INF/struts-config.xml` (used as reference, then deleted)

---

## Step 2: Controller Layer Migration (Actions → Controllers)

### 2.1 Action Class Transformation

#### Pattern: Detect Struts Actions
**Detection Criteria**:
- Class extends `org.apache.struts.action.Action`
- Contains method with signature: `execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)`
- Typically located in `*.action.*` package

#### Pattern: Transform Action to Controller

**Input Example**:
```java
package com.example.action;

import org.apache.struts.action.*;
import javax.servlet.http.*;

public class ListItemsAction extends Action {
    private ItemDAO itemDAO = new ItemDAO();

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            List<Item> items = itemDAO.findAll();
            request.setAttribute("items", items);
            return mapping.findForward("success");
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            return mapping.findForward("error");
        }
    }
}
```

**Output Template**:
```java
package com.example.controller;  // CHANGE: *.action → *.controller

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;

@Controller  // ADD: Spring stereotype annotation
@RequestMapping("/path")  // ADD: derived from struts-config.xml mappings
public class ItemController {  // CHANGE: Remove "Action" suffix, add "Controller"

    // CHANGE: Field injection → Constructor injection
    private final ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/list")  // ADD: HTTP method mapping from struts-config
    public String listItems(Model model) {  // CHANGE: Return String, use Model
        try {
            List<Item> items = itemRepository.findAll();
            model.addAttribute("items", items);  // CHANGE: request.setAttribute → model.addAttribute
            return "item/list";  // CHANGE: ActionForward → view name string
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}
```

### 2.2 Action-to-Controller Transformation Rules

#### Rule 2.2.1: Class Declaration
| Aspect | From | To |
|--------|------|-----|
| Package | `*.action.*` | `*.controller.*` |
| Extends | `extends Action` | Remove (no superclass) |
| Annotations | None | `@Controller` + `@RequestMapping` |
| Naming | `*Action` | `*Controller` |

#### Rule 2.2.2: Dependency Management
**Pattern**: Detect field initialization
```java
private SomeDAO someDAO = new SomeDAO();
```

**Transform to**:
```java
private final SomeRepository someRepository;

@Autowired
public ControllerName(SomeRepository someRepository) {
    this.someRepository = someRepository;
}
```

**Rules**:
- All DAO references → Repository references (transformed in Step 3)
- Use constructor injection (final fields)
- Add `@Autowired` annotation (optional in single-constructor case)

#### Rule 2.2.3: Method Transformation
**Pattern**: `execute()` method detection
```java
public ActionForward execute(ActionMapping mapping,
                            ActionForm form,
                            HttpServletRequest request,
                            HttpServletResponse response)
```

**Transform to**:
```java
@GetMapping("/endpoint")  // or @PostMapping based on struts-config
public String methodName(Model model,
                        @Valid FormDTO formDTO,  // if form exists
                        BindingResult result)    // if validation exists
```

**Mapping Rules**:

| Struts Concept | Spring Boot Equivalent |
|---------------|------------------------|
| `ActionMapping mapping` | Remove (routing via annotations) |
| `ActionForm form` | Typed DTO parameter with `@ModelAttribute` or `@Valid` |
| `HttpServletRequest request` | Keep only if directly accessed, prefer `Model` |
| `HttpServletResponse response` | Keep only if directly manipulated |
| `ActionForward` return | `String` (view name) or `ResponseEntity<T>` for REST |

#### Rule 2.2.4: Request Parameter Handling

**Pattern**: Extract parameters from request
```java
String id = request.getParameter("id");
String name = request.getParameter("name");
```

**Transform to**:
```java
public String method(@RequestParam("id") String id,
                    @RequestParam("name") String name)
```

**Pattern**: Extract path parameters
```java
// Struts typically uses query params, no path variables
String id = request.getParameter("id");
```

**Transform to (Modern REST)**:
```java
@GetMapping("/items/{id}")
public String method(@PathVariable("id") Long id)
```

#### Rule 2.2.5: Model Data Passing

**Pattern**: Setting request attributes
```java
request.setAttribute("attributeName", value);
```

**Transform to**:
```java
model.addAttribute("attributeName", value);
```

**Pattern**: Getting request attributes
```java
Object value = request.getAttribute("attributeName");
```

**Transform to**:
```java
// Usually not needed - attributes set in same method
// If needed across requests, use @SessionAttributes or RedirectAttributes
```

#### Rule 2.2.6: Navigation and Forwards

**Pattern**: Forward to success view
```java
return mapping.findForward("success");
```

**Reference**: Check struts-config.xml for forward mappings
```xml
<action path="/listItems" type="com.example.action.ListItemsAction">
    <forward name="success" path="/WEB-INF/jsp/items/list.jsp"/>
</action>
```

**Transform to**:
```java
return "items/list";  // Remove /WEB-INF/jsp/ prefix and .jsp suffix
```

**Pattern**: Redirect after POST
```java
return mapping.findForward("success");  // with redirect="true" in config
```

**Transform to**:
```java
return "redirect:/list";
```

**Pattern**: Global forwards
```xml
<global-forwards>
    <forward name="welcome" path="/welcome.do"/>
    <forward name="error" path="/error.jsp"/>
</global-forwards>
```

**Transform to**: Hardcode the path or use constants
```java
return "redirect:/welcome";
return "error";
```

#### Rule 2.2.7: Error Handling

**Pattern**: Try-catch with error forward
```java
try {
    // business logic
    return mapping.findForward("success");
} catch (Exception e) {
    request.setAttribute("errorMessage", e.getMessage());
    return mapping.findForward("error");
}
```

**Transform to** (Option 1 - Controller-level):
```java
try {
    // business logic
    return "success";
} catch (Exception e) {
    model.addAttribute("errorMessage", e.getMessage());
    return "error";
}
```

**Transform to** (Option 2 - Global exception handler):
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }
}
```

**Recommendation**: Use Option 2 for centralized error handling

#### Rule 2.2.8: HTTP Method Mapping

**Derive from struts-config.xml**:
```xml
<action path="/saveItem"
        type="com.example.action.SaveItemAction"
        name="itemForm"
        validate="true"
        input="/edit.jsp">
```

**Analysis**:
- Has `name` attribute (form) → Likely POST
- Has `validate="true"` → Definitely POST
- Has `input` attribute → POST with validation

**Mapping**:
```java
@PostMapping("/save")
public String saveItem(@Valid @ModelAttribute ItemDTO itemDTO,
                      BindingResult result,
                      RedirectAttributes redirectAttributes)
```

**Default Rules**:
| Struts Pattern | HTTP Method |
|---------------|-------------|
| Action has ActionForm parameter + validate="true" | `@PostMapping` |
| Action name contains "save", "create", "update", "delete" | `@PostMapping` |
| Action name contains "list", "view", "show", "display", "edit" (prep) | `@GetMapping` |
| Default (no indicators) | `@GetMapping` |

### 2.3 URL Path Derivation

#### Pattern: Extract from struts-config.xml
```xml
<action path="/listAccounts" type="com.redhat.bank.action.ListAccountsAction">
```

**Transform to**:
```java
@Controller
@RequestMapping("/accounts")  // Base path (plural of entity)
public class AccountController {

    @GetMapping("/list")  // Specific action
    // OR
    @GetMapping  // If it's the main listing endpoint
}
```

**Path Construction Rules**:
1. Identify primary entity from Action class name (`ListAccountsAction` → `Account`)
2. Use plural form for base path (`/accounts`)
3. Use action-specific subpaths (`/list`, `/view/{id}`, `/edit/{id}`, `/save`)
4. Prefer RESTful conventions where possible

**RESTful Path Recommendations**:
| Struts Path | Action Type | RESTful Spring Path | HTTP Method |
|-------------|-------------|---------------------|-------------|
| `/listItems` | List all | `/items` | GET |
| `/viewItem?id=1` | View one | `/items/{id}` | GET |
| `/editItem?id=1` | Edit form | `/items/{id}/edit` | GET |
| `/createItem` | Create form | `/items/new` | GET |
| `/saveItem` | Save/Update | `/items` or `/items/{id}` | POST/PUT |
| `/deleteItem?id=1` | Delete | `/items/{id}` | DELETE or POST |

### 2.4 Session and Flash Attributes

#### Pattern: Session attributes in Struts
```java
request.getSession().setAttribute("user", user);
Object user = request.getSession().getAttribute("user");
```

**Transform to**:
```java
@Controller
@SessionAttributes("user")  // Declare at class level
public class SomeController {

    @GetMapping("/endpoint")
    public String method(Model model) {
        model.addAttribute("user", user);  // Automatically stored in session
    }
}
```

#### Pattern: Flash attributes (survive redirect)
```java
request.setAttribute("message", "Success!");
return mapping.findForward("success");  // with redirect="true"
```

**Transform to**:
```java
public String method(RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("message", "Success!");
    return "redirect:/list";
}
```

---

## Step 3: Form Layer Migration (ActionForms → DTOs)

### 3.1 ActionForm Transformation

#### Pattern: Detect ActionForm Classes
**Detection Criteria**:
- Class extends `org.apache.struts.action.ActionForm`
- Contains fields (typically all String type)
- Contains `validate()` method returning `ActionErrors`
- Contains `reset()` method
- Typically located in `*.form.*` package

#### Pattern: Transform ActionForm to DTO

**Input Example**:
```java
package com.example.form;

import org.apache.struts.action.*;
import javax.servlet.http.*;

public class AccountForm extends ActionForm {
    private String id;
    private String accountNumber;
    private String holderName;
    private String accountType;
    private String balance;
    private String email;

    // Getters and setters

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.id = null;
        this.accountNumber = null;
        // ... reset all fields
    }

    public ActionErrors validate(ActionMapping mapping,
                                HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (holderName == null || holderName.trim().isEmpty()) {
            errors.add("holderName",
                new ActionMessage("error.holderName.required"));
        }

        if (balance != null && !balance.isEmpty()) {
            try {
                Double.parseDouble(balance);
            } catch (NumberFormatException e) {
                errors.add("balance",
                    new ActionMessage("error.balance.invalid"));
            }
        }

        if (email != null && !email.isEmpty() && !email.contains("@")) {
            errors.add("email",
                new ActionMessage("error.email.invalid"));
        }

        return errors;
    }
}
```

**Output Template**:
```java
package com.example.dto;  // CHANGE: *.form → *.dto

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AccountDTO {  // CHANGE: Remove "Form" suffix, add "DTO"

    // CHANGE: Use proper types, not all Strings
    private Long id;

    private String accountNumber;

    @NotBlank(message = "Holder name is required")  // TRANSFORM: validation logic
    private String holderName;

    @NotBlank(message = "Account type is required")
    private String accountType;

    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    private BigDecimal balance;  // CHANGE: String → BigDecimal

    @Email(message = "Invalid email format")
    private String email;

    // Getters and setters
    // REMOVE: reset() method
    // REMOVE: validate() method
}
```

### 3.2 ActionForm-to-DTO Transformation Rules

#### Rule 3.2.1: Class Declaration
| Aspect | From | To |
|--------|------|-----|
| Package | `*.form.*` | `*.dto.*` or `*.model.*` |
| Extends | `extends ActionForm` | Remove (POJO) |
| Annotations | None | Import `jakarta.validation.constraints.*` |
| Naming | `*Form` | `*DTO` or `*Request`/`*Response` |

#### Rule 3.2.2: Field Type Transformation
**Problem**: Struts ActionForms use String for all fields to handle conversion errors

**Solution**: Use proper Java types with Bean Validation

| String Field Pattern | Target Type | Validation Annotations |
|---------------------|-------------|----------------------|
| ID fields (`id`, `*Id`) | `Long` or `UUID` | None (nullable for create) |
| Numeric fields | `Integer`, `Long`, `BigDecimal` | `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax` |
| Boolean flags | `Boolean` | None |
| Date fields | `LocalDate`, `LocalDateTime` | `@Past`, `@Future`, `@PastOrPresent` |
| Email fields | `String` | `@Email` |
| Enums (type, status) | `Enum` | None (type safety built-in) |
| Required text | `String` | `@NotBlank` |
| Optional text | `String` | None |
| Collections | `List<T>`, `Set<T>` | `@Size`, `@NotEmpty` |

#### Rule 3.2.3: Validation Migration

**Struts Validation Pattern → Bean Validation Annotation**:

| Struts Validation Code | Bean Validation Annotation |
|------------------------|---------------------------|
| `if (field == null \|\| field.trim().isEmpty())` | `@NotBlank` |
| `if (field == null)` | `@NotNull` |
| `if (field.length() < min \|\| field.length() > max)` | `@Size(min=X, max=Y)` |
| `if (!field.contains("@"))` | `@Email` |
| `if (Double.parseDouble(field) < 0)` | `@DecimalMin("0.0")` |
| `if (Integer.parseInt(field) < min)` | `@Min(value)` |
| `if (!Pattern.matches(regex, field))` | `@Pattern(regexp="...")` |
| Custom business logic | Create custom validator or move to service |

**Complex Validation Example**:

**Struts**:
```java
public ActionErrors validate(ActionMapping mapping,
                            HttpServletRequest request) {
    ActionErrors errors = new ActionErrors();

    if (amount != null && !amount.isEmpty()) {
        try {
            double amt = Double.parseDouble(amount);
            if (amt <= 0) {
                errors.add("amount",
                    new ActionMessage("error.amount.positive"));
            }
            if (amt > 1000000) {
                errors.add("amount",
                    new ActionMessage("error.amount.limit"));
            }
        } catch (NumberFormatException e) {
            errors.add("amount",
                new ActionMessage("error.amount.invalid"));
        }
    }

    return errors;
}
```

**Spring Boot**:
```java
@NotNull(message = "Amount is required")
@DecimalMin(value = "0.01", message = "Amount must be positive")
@DecimalMax(value = "1000000", message = "Amount cannot exceed 1,000,000")
private BigDecimal amount;
```

#### Rule 3.2.4: Cross-Field Validation

**Pattern**: Validation involving multiple fields
```java
if (startDate != null && endDate != null) {
    if (endDate.before(startDate)) {
        errors.add("endDate",
            new ActionMessage("error.endDate.beforeStart"));
    }
}
```

**Transform to**: Custom class-level validator
```java
@DateRange(message = "End date must be after start date")
public class ReservationDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}

// Create custom validator
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {
    String message() default "Invalid date range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

#### Rule 3.2.5: Remove Unnecessary Methods

**Methods to DELETE**:
1. `reset(ActionMapping, HttpServletRequest)` - Spring handles form initialization
2. `validate(ActionMapping, HttpServletRequest)` - Replaced by Bean Validation annotations
3. Any Struts-specific imports or dependencies

**Methods to KEEP**:
1. Getters and setters (standard JavaBean pattern)
2. `toString()`, `equals()`, `hashCode()` if present
3. Business logic methods (rare, should move to service layer)

#### Rule 3.2.6: Enum Transformation

**Pattern**: String field with limited values
```java
private String accountType;  // "SAVINGS", "CHECKING", "CREDIT"

public ActionErrors validate(...) {
    if (accountType != null &&
        !accountType.equals("SAVINGS") &&
        !accountType.equals("CHECKING") &&
        !accountType.equals("CREDIT")) {
        errors.add("accountType", new ActionMessage("error.accountType.invalid"));
    }
}
```

**Transform to**: Enum type
```java
public enum AccountType {
    SAVINGS, CHECKING, CREDIT
}

// In DTO
private AccountType accountType;  // Type safety, no validation needed
```

### 3.3 Controller Integration

#### Pattern: Using DTO in Controller

**Struts**:
```java
public ActionForward execute(ActionMapping mapping,
                            ActionForm form, ...) {
    AccountForm accountForm = (AccountForm) form;
    // ... use accountForm
}
```

**Spring Boot**:
```java
@PostMapping("/save")
public String saveAccount(@Valid @ModelAttribute AccountDTO accountDTO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        return "account/edit";  // Return to form with errors
    }

    // ... process valid DTO
    redirectAttributes.addFlashAttribute("message", "Account saved successfully");
    return "redirect:/accounts";
}
```

**Key Annotations**:
- `@Valid`: Triggers Bean Validation
- `@ModelAttribute`: Binds form data to DTO (can be omitted, implied for POJOs)
- `BindingResult`: Contains validation errors (must be immediately after validated parameter)

---

## Step 4: Data Access Layer Migration (DAO → Repository)

### 4.1 DAO Pattern Detection

#### Pattern: Identify DAO Classes
**Detection Criteria**:
- Class name ends with `DAO` or `Dao`
- Contains JDBC code: `Connection`, `PreparedStatement`, `ResultSet`
- Methods like `findAll()`, `findById()`, `save()`, `update()`, `delete()`
- Typically located in `*.dao.*` package
- May have companion entity/model classes

### 4.2 Entity Class Creation

#### Pattern: Extract Entity from DAO

**Detection**: Look for `mapResultSetToEntity()` methods or similar
```java
private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
    Account account = new Account();
    account.setId(rs.getLong("id"));
    account.setAccountNumber(rs.getString("account_number"));
    account.setHolderName(rs.getString("holder_name"));
    account.setAccountType(rs.getString("account_type"));
    account.setBalance(rs.getBigDecimal("balance"));
    account.setEmail(rs.getString("email"));
    account.setPhone(rs.getString("phone"));
    account.setCreatedDate(rs.getTimestamp("created_date"));
    account.setActive(rs.getBoolean("active"));
    return account;
}
```

**Extract Schema**: Look for CREATE TABLE statements or schema initialization
```java
String createTable = "CREATE TABLE accounts ("
    + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
    + "account_number VARCHAR(20) UNIQUE,"
    + "holder_name VARCHAR(100),"
    + "account_type VARCHAR(20),"
    + "balance DECIMAL(15,2),"
    + "email VARCHAR(100),"
    + "phone VARCHAR(20),"
    + "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
    + "active BOOLEAN DEFAULT TRUE"
    + ")";
```

#### Pattern: Create JPA Entity

**If Entity Already Exists** (Plain POJO):
```java
package com.example.model;

public class Account {
    private Long id;
    private String accountNumber;
    private String holderName;
    // ... other fields
    // ... getters/setters
}
```

**Transform to JPA Entity**:
```java
package com.example.entity;  // CHANGE: *.model → *.entity

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity  // ADD: JPA annotations
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, length = 20)
    private String accountNumber;

    @Column(name = "holder_name", length = 100)
    private String holderName;

    @Column(name = "account_type", length = 20)
    private String accountType;  // Consider making this an Enum

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    // Getters and setters

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
```

#### Rule 4.2.1: SQL-to-JPA Column Mapping

| SQL Type | Java Type | JPA Annotation |
|----------|-----------|----------------|
| `BIGINT`, `INTEGER` | `Long`, `Integer` | `@Column` |
| `VARCHAR(n)` | `String` | `@Column(length = n)` |
| `DECIMAL(p,s)` | `BigDecimal` | `@Column(precision = p, scale = s)` |
| `BOOLEAN` | `Boolean` | `@Column` |
| `TIMESTAMP`, `DATE` | `LocalDateTime`, `LocalDate` | `@Column` |
| `AUTO_INCREMENT` / `SERIAL` | `Long` | `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `UNIQUE` constraint | - | `@Column(unique = true)` |
| `PRIMARY KEY` | - | `@Id` |

#### Rule 4.2.2: Entity Lifecycle Callbacks

**Pattern**: DAO sets default values
```java
public void create(Account account) {
    if (account.getCreatedDate() == null) {
        account.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    }
    if (account.getActive() == null) {
        account.setActive(true);
    }
    // ... insert logic
}
```

**Transform to**: JPA lifecycle callbacks
```java
@Entity
public class Account {
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    private Boolean active = true;  // Default in field declaration

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
}
```

### 4.3 Repository Interface Creation

#### Pattern: DAO to Repository Transformation

**Input (JDBC DAO)**:
```java
package com.example.dao;

import java.sql.*;
import java.util.*;

public class AccountDAO {

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(
                "SELECT * FROM accounts WHERE active = TRUE ORDER BY id");
            rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding accounts", e);
        } finally {
            closeResources(conn, stmt, rs);
        }

        return accounts;
    }

    public Account findById(Long id) {
        // ... similar pattern
        String sql = "SELECT * FROM accounts WHERE id = ? AND active = TRUE";
        // ... execute query
    }

    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        // ... execute query
    }

    public void create(Account account) {
        String sql = "INSERT INTO accounts (account_number, holder_name, ...) "
                   + "VALUES (?, ?, ...)";
        // ... execute update with generated keys
    }

    public void update(Account account) {
        String sql = "UPDATE accounts SET account_number = ?, holder_name = ?, ... "
                   + "WHERE id = ?";
        // ... execute update
    }

    public void delete(Long id) {
        // Soft delete
        String sql = "UPDATE accounts SET active = FALSE WHERE id = ?";
        // ... execute update
    }

    public void deposit(Long accountId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        // ... execute update
    }

    public void withdraw(Long accountId, BigDecimal amount) {
        String sql = "UPDATE accounts SET balance = balance - ? "
                   + "WHERE id = ? AND balance >= ?";
        // ... execute update with balance check
    }
}
```

**Output (Spring Data JPA Repository)**:
```java
package com.example.repository;

import com.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // findAll() - inherited from JpaRepository

    // findById() - inherited from JpaRepository (returns Optional<Account>)

    // Derived query method
    Optional<Account> findByAccountNumber(String accountNumber);

    // Derived query with condition
    List<Account> findByActiveTrue();  // Replaces: WHERE active = TRUE

    // save() - inherited (handles both create and update)

    // delete() - inherited (hard delete)
    // For soft delete, use custom query:
    @Modifying
    @Query("UPDATE Account a SET a.active = false WHERE a.id = :id")
    void softDelete(@Param("id") Long id);

    // Custom query for deposit
    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.id = :id")
    void deposit(@Param("id") Long accountId, @Param("amount") BigDecimal amount);

    // Custom query for withdraw with validation
    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance - :amount "
         + "WHERE a.id = :id AND a.balance >= :amount")
    int withdraw(@Param("id") Long accountId, @Param("amount") BigDecimal amount);
}
```

### 4.4 DAO-to-Repository Transformation Rules

#### Rule 4.4.1: Basic CRUD Mapping

| DAO Method | Repository Method | Notes |
|------------|------------------|-------|
| `List<T> findAll()` | `List<T> findAll()` | Inherited from JpaRepository |
| `T findById(Long id)` | `Optional<T> findById(Long id)` | Returns Optional, handle with `.orElse()` |
| `void create(T entity)` | `T save(T entity)` | Same method for create/update |
| `void update(T entity)` | `T save(T entity)` | JPA detects if entity exists |
| `void delete(Long id)` | `void deleteById(Long id)` | Hard delete |
| `void deleteAll()` | `void deleteAll()` | Inherited |
| `long count()` | `long count()` | Inherited |
| `boolean exists(Long id)` | `boolean existsById(Long id)` | Inherited |

#### Rule 4.4.2: Query Method Derivation

**Pattern**: Simple WHERE clause
```java
// DAO
public Account findByEmail(String email) {
    String sql = "SELECT * FROM accounts WHERE email = ?";
    // ... execute
}
```

**Transform to**: Derived query method
```java
// Repository
Optional<Account> findByEmail(String email);
```

**Spring Data Query Derivation Keywords**:

| SQL Pattern | Repository Method Name |
|-------------|----------------------|
| `WHERE field = ?` | `findByField(Type field)` |
| `WHERE field1 = ? AND field2 = ?` | `findByField1AndField2(Type1 f1, Type2 f2)` |
| `WHERE field1 = ? OR field2 = ?` | `findByField1OrField2(...)` |
| `WHERE field LIKE ?` | `findByFieldContaining(String field)` |
| `WHERE field LIKE 'prefix%'` | `findByFieldStartingWith(String field)` |
| `WHERE field LIKE '%suffix'` | `findByFieldEndingWith(String field)` |
| `WHERE field > ?` | `findByFieldGreaterThan(Type field)` |
| `WHERE field < ?` | `findByFieldLessThan(Type field)` |
| `WHERE field BETWEEN ? AND ?` | `findByFieldBetween(Type start, Type end)` |
| `WHERE field IS NULL` | `findByFieldIsNull()` |
| `WHERE field IS NOT NULL` | `findByFieldIsNotNull()` |
| `WHERE field IN (?)` | `findByFieldIn(Collection<Type> fields)` |
| `WHERE field = TRUE` | `findByFieldTrue()` |
| `WHERE field = FALSE` | `findByFieldFalse()` |
| `ORDER BY field ASC` | `findByXxxOrderByFieldAsc(...)` |
| `ORDER BY field DESC` | `findByXxxOrderByFieldDesc(...)` |

#### Rule 4.4.3: Complex Queries with @Query

**Pattern**: Complex JOIN or calculation
```java
// DAO
public List<Account> findAccountsWithHighBalance(BigDecimal threshold) {
    String sql = "SELECT * FROM accounts "
               + "WHERE balance > ? AND active = TRUE "
               + "ORDER BY balance DESC";
    // ... execute
}
```

**Transform to**: @Query annotation
```java
// Repository
@Query("SELECT a FROM Account a WHERE a.balance > :threshold AND a.active = true ORDER BY a.balance DESC")
List<Account> findAccountsWithHighBalance(@Param("threshold") BigDecimal threshold);
```

**Pattern**: Native SQL (when JPQL is complex)
```java
// DAO with complex SQL
public List<Object[]> getAccountStatsByType() {
    String sql = "SELECT account_type, COUNT(*), SUM(balance) "
               + "FROM accounts GROUP BY account_type";
    // ... execute
}
```

**Transform to**: Native query
```java
// Repository
@Query(value = "SELECT account_type, COUNT(*), SUM(balance) "
             + "FROM accounts GROUP BY account_type",
       nativeQuery = true)
List<Object[]> getAccountStatsByType();
```

#### Rule 4.4.4: Modifying Queries

**Pattern**: UPDATE or DELETE operations
```java
// DAO
public void updateEmail(Long id, String email) {
    String sql = "UPDATE accounts SET email = ? WHERE id = ?";
    // ... execute update
}
```

**Transform to**: @Modifying annotation (REQUIRED for updates/deletes)
```java
// Repository
@Modifying
@Query("UPDATE Account a SET a.email = :email WHERE a.id = :id")
void updateEmail(@Param("id") Long id, @Param("email") String email);
```

**CRITICAL**: @Modifying queries must be used in @Transactional context

### 4.5 Service Layer Creation

#### Pattern: Business Logic Extraction

**Problem**: DAOs often contain business logic that should be in services
```java
// DAO method mixing data access and business logic
public void deposit(Long accountId, BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }

    Account account = findById(accountId);
    if (account == null) {
        throw new RuntimeException("Account not found");
    }

    String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
    // ... execute
}
```

**Solution**: Create service layer
```java
package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void deposit(Long accountId, BigDecimal amount) {
        // Business validation
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Data access
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // Business logic
        account.setBalance(account.getBalance().add(amount));

        // Save (automatic in @Transactional context)
        accountRepository.save(account);
    }

    public List<Account> getAllActiveAccounts() {
        return accountRepository.findByActiveTrue();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found: " + id));
    }
}
```

#### Rule 4.5.1: Service Layer Responsibilities

**Service Layer SHOULD**:
- Contain business logic
- Validate business rules
- Coordinate between multiple repositories
- Handle transaction boundaries (`@Transactional`)
- Throw business exceptions
- Map between entities and DTOs

**Repository Layer SHOULD**:
- Only handle data access
- Contain no business logic
- Be simple interface definitions
- Use derived queries or @Query annotations

### 4.6 Transaction Management

#### Pattern: Manual Transaction Management in DAO
```java
public void transferFunds(Long fromId, Long toId, BigDecimal amount) {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);  // Start transaction

        withdraw(fromId, amount);
        deposit(toId, amount);

        conn.commit();  // Commit transaction
    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // handle
            }
        }
        throw new RuntimeException("Transfer failed", e);
    } finally {
        // cleanup
    }
}
```

**Transform to**: Declarative transactions
```java
@Service
public class AccountService {

    @Transactional  // Automatic transaction management
    public void transferFunds(Long fromId, Long toId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromId)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account toAccount = accountRepository.findById(toId)
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Automatic commit if no exception
        // Automatic rollback if exception thrown
    }
}
```

**Transaction Annotations**:
- `@Transactional` at class level: All methods transactional
- `@Transactional` at method level: Only that method transactional
- `@Transactional(readOnly = true)`: Optimization for read-only operations
- Default propagation: `REQUIRED` (join existing or create new)

### 4.7 Database Configuration Migration

#### Pattern: Detect Manual Connection Management
```java
public class DatabaseConnection {
    private static final String URL = "jdbc:h2:mem:bankdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

**Transform to**: application.yml configuration
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:bankdb
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: update  # Or validate, create, create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect

  h2:
    console:
      enabled: true  # Access at /h2-console
```

#### Pattern: Schema Initialization
**Struts** (In Java code):
```java
String createTable = "CREATE TABLE IF NOT EXISTS accounts (...)";
stmt.execute(createTable);
```

**Spring Boot** (SQL file):
Create `src/main/resources/schema.sql`:
```sql
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE,
    holder_name VARCHAR(100),
    -- ... all columns
);
```

Create `src/main/resources/data.sql` for sample data:
```sql
INSERT INTO accounts (account_number, holder_name, account_type, balance)
VALUES ('ACC001', 'John Doe', 'SAVINGS', 1000.00);
```

**Configuration**:
```yaml
spring:
  sql:
    init:
      mode: always  # Or embedded (only for embedded DBs)
```

---

## Step 5: View Layer Migration (JSP → Thymeleaf)

### 5.1 JSP to Thymeleaf Transformation

#### Pattern: Basic JSP Structure
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Account List</title>
</head>
<body>
    <h1>Accounts</h1>
    <logic:iterate name="accounts" id="account">
        <p><bean:write name="account" property="holderName"/></p>
    </logic:iterate>
</body>
</html>
```

**Transform to**: Thymeleaf template
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Account List</title>
</head>
<body>
    <h1>Accounts</h1>
    <div th:each="account : ${accounts}">
        <p th:text="${account.holderName}"></p>
    </div>
</body>
</html>
```

### 5.2 Tag Library Migration

#### Rule 5.2.1: Struts HTML Tags → Thymeleaf

| Struts Tag | Thymeleaf Equivalent |
|-----------|---------------------|
| `<html:form action="/save">` | `<form th:action="@{/save}" method="post">` |
| `<html:text property="name"/>` | `<input type="text" th:field="*{name}"/>` |
| `<html:password property="password"/>` | `<input type="password" th:field="*{password}"/>` |
| `<html:textarea property="description"/>` | `<textarea th:field="*{description}"></textarea>` |
| `<html:select property="type">` | `<select th:field="*{type}">` |
| `<html:option value="val">Label</html:option>` | `<option th:value="val">Label</option>` |
| `<html:checkbox property="active"/>` | `<input type="checkbox" th:field="*{active}"/>` |
| `<html:radio property="gender" value="M"/>` | `<input type="radio" th:field="*{gender}" th:value="M"/>` |
| `<html:hidden property="id"/>` | `<input type="hidden" th:field="*{id}"/>` |
| `<html:submit value="Save"/>` | `<button type="submit">Save</button>` |
| `<html:cancel/>` | `<button type="button" th:onclick="'location.href=\'' + @{/cancel} + '\''">Cancel</button>` |
| `<html:link page="/view">Link</html:link>` | `<a th:href="@{/view}">Link</a>` |
| `<html:errors property="name"/>` | `<span th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></span>` |

#### Rule 5.2.2: Struts Bean Tags → Thymeleaf

| Struts Bean Tag | Thymeleaf Equivalent |
|----------------|---------------------|
| `<bean:write name="account" property="name"/>` | `<span th:text="${account.name}"></span>` |
| `<bean:write name="balance" format="$#,##0.00"/>` | `<span th:text="${#numbers.formatCurrency(balance)}"></span>` |
| `<bean:message key="label.welcome"/>` | `<span th:text="#{label.welcome}"></span>` |

#### Rule 5.2.3: Struts Logic Tags → Thymeleaf

| Struts Logic Tag | Thymeleaf Equivalent |
|-----------------|---------------------|
| `<logic:iterate name="list" id="item">` | `<div th:each="item : ${list}">` |
| `<logic:notEmpty name="list">` | `<div th:if="${not #lists.isEmpty(list)}">` |
| `<logic:empty name="list">` | `<div th:if="${#lists.isEmpty(list)}">` |
| `<logic:equal name="status" value="ACTIVE">` | `<div th:if="${status == 'ACTIVE'}">` |
| `<logic:notEqual name="status" value="INACTIVE">` | `<div th:if="${status != 'INACTIVE'}">` |
| `<logic:greaterThan name="count" value="10">` | `<div th:if="${count > 10}">` |
| `<logic:present name="user">` | `<div th:if="${user != null}">` |
| `<logic:notPresent name="user">` | `<div th:if="${user == null}">` |

#### Rule 5.2.4: JSTL Tags → Thymeleaf

| JSTL Tag | Thymeleaf Equivalent |
|---------|---------------------|
| `<c:forEach var="item" items="${list}">` | `<div th:each="item : ${list}">` |
| `<c:if test="${condition}">` | `<div th:if="${condition}">` |
| `<c:choose><c:when test="${x}">...</c:when><c:otherwise>...</c:otherwise></c:choose>` | `<div th:if="${x}">...</div><div th:unless="${x}">...</div>` |
| `<c:set var="name" value="${value}"/>` | `<div th:with="name=${value}">` |
| `<c:out value="${text}"/>` | `<span th:text="${text}"></span>` |
| `<fmt:formatNumber value="${balance}" type="currency"/>` | `<span th:text="${#numbers.formatCurrency(balance)}"></span>` |
| `<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>` | `<span th:text="${#temporals.format(date, 'yyyy-MM-dd')}"></span>` |

### 5.3 Form Binding

#### Pattern: Struts Form Binding
```jsp
<html:form action="/saveAccount" focus="holderName">
    <html:hidden property="id"/>

    Account Number: <html:text property="accountNumber" readonly="true"/>
    <html:errors property="accountNumber"/>

    Holder Name: <html:text property="holderName"/>
    <html:errors property="holderName"/>

    Type: <html:select property="accountType">
        <html:option value="SAVINGS">Savings</html:option>
        <html:option value="CHECKING">Checking</html:option>
    </html:select>

    <html:submit value="Save"/>
</html:form>
```

**Transform to**: Thymeleaf form
```html
<form th:action="@{/accounts/save}" th:object="${accountDTO}" method="post">
    <input type="hidden" th:field="*{id}"/>

    <div>
        <label>Account Number:</label>
        <input type="text" th:field="*{accountNumber}" readonly/>
        <span th:if="${#fields.hasErrors('accountNumber')}"
              th:errors="*{accountNumber}"
              class="error"></span>
    </div>

    <div>
        <label>Holder Name:</label>
        <input type="text" th:field="*{holderName}" autofocus/>
        <span th:if="${#fields.hasErrors('holderName')}"
              th:errors="*{holderName}"
              class="error"></span>
    </div>

    <div>
        <label>Type:</label>
        <select th:field="*{accountType}">
            <option th:value="SAVINGS">Savings</option>
            <option th:value="CHECKING">Checking</option>
        </select>
    </div>

    <button type="submit">Save</button>
</form>
```

**Key Concepts**:
- `th:object="${formObject}"`: Binds form to model attribute
- `th:field="*{property}"`: Binds input to property (handles name, id, value)
- `th:errors="*{property}"`: Displays validation errors
- `@{/path}`: URL expression (handles context path)
- `*{property}`: Selection variable (relative to th:object)
- `${variable}`: Normal variable expression

### 5.4 Iteration and Display

#### Pattern: Display List with Actions
```jsp
<table>
    <logic:iterate name="accounts" id="account">
        <tr>
            <td><bean:write name="account" property="accountNumber"/></td>
            <td><bean:write name="account" property="holderName"/></td>
            <td><fmt:formatNumber value="${account.balance}" type="currency"/></td>
            <td>
                <html:link page="/viewAccount.do" paramId="id" paramName="account" paramProperty="id">
                    View
                </html:link>
                <html:link page="/editAccount.do" paramId="id" paramName="account" paramProperty="id">
                    Edit
                </html:link>
            </td>
        </tr>
    </logic:iterate>
</table>
```

**Transform to**:
```html
<table>
    <tr th:each="account : ${accounts}">
        <td th:text="${account.accountNumber}"></td>
        <td th:text="${account.holderName}"></td>
        <td th:text="${#numbers.formatCurrency(account.balance)}"></td>
        <td>
            <a th:href="@{/accounts/{id}(id=${account.id})}">View</a>
            <a th:href="@{/accounts/{id}/edit(id=${account.id})}">Edit</a>
        </td>
    </tr>
</table>
```

### 5.5 Conditional Rendering

#### Pattern: Conditional Display
```jsp
<logic:notEmpty name="accounts">
    <p>Total accounts: ${fn:length(accounts)}</p>
    <!-- display accounts -->
</logic:notEmpty>

<logic:empty name="accounts">
    <p>No accounts found.</p>
</logic:empty>

<c:if test="${account.balance > 1000}">
    <span class="high-balance">Premium Account</span>
</c:if>
```

**Transform to**:
```html
<div th:if="${not #lists.isEmpty(accounts)}">
    <p th:text="'Total accounts: ' + ${#lists.size(accounts)}"></p>
    <!-- display accounts -->
</div>

<div th:if="${#lists.isEmpty(accounts)}">
    <p>No accounts found.</p>
</div>

<span th:if="${account.balance > 1000}" class="high-balance">
    Premium Account
</span>
```

### 5.6 Internationalization (i18n)

#### Pattern: Message Resources
**Struts** (MessageResources.properties):
```properties
label.welcome=Welcome to Bank Application
label.account.number=Account Number
label.account.holderName=Holder Name
error.account.notfound=Account not found
```

**Usage in JSP**:
```jsp
<bean:message key="label.welcome"/>
```

**Spring Boot**: Use same file, rename to `messages.properties`
```properties
label.welcome=Welcome to Bank Application
label.account.number=Account Number
```

**Usage in Thymeleaf**:
```html
<h1 th:text="#{label.welcome}"></h1>
<label th:text="#{label.account.number}"></label>
```

**Configuration** (if needed):
```yaml
spring:
  messages:
    basename: messages  # Default location: classpath:messages.properties
    encoding: UTF-8
```

### 5.7 Static Resources

#### Pattern: Extract Embedded CSS
**Struts JSP** (common pattern):
```jsp
<head>
    <style>
        body { font-family: Arial; background: #f0f0f0; }
        .card { border: 1px solid #ccc; padding: 20px; }
        /* ... hundreds of lines ... */
    </style>
</head>
```

**Transform to**:
1. Create `src/main/resources/static/css/styles.css`
2. Move all CSS to this file
3. Reference in Thymeleaf:
```html
<head>
    <link rel="stylesheet" th:href="@{/css/styles.css}"/>
</head>
```

**Static Resource Locations** (auto-served by Spring Boot):
- `src/main/resources/static/` - CSS, JS, images
- `src/main/resources/public/` - Public assets
- `src/main/resources/resources/` - General resources
- `src/main/resources/META-INF/resources/` - Library resources

---

## Step 6: Configuration Migration

### 6.1 struts-config.xml Analysis and Removal

#### Pattern: Extract Routing Information
**struts-config.xml**:
```xml
<struts-config>
    <form-beans>
        <form-bean name="accountForm" type="com.example.form.AccountForm"/>
    </form-beans>

    <action-mappings>
        <action path="/listAccounts"
                type="com.example.action.ListAccountsAction">
            <forward name="success" path="/WEB-INF/jsp/account/list.jsp"/>
        </action>

        <action path="/saveAccount"
                type="com.example.action.SaveAccountAction"
                name="accountForm"
                scope="request"
                validate="true"
                input="/WEB-INF/jsp/account/edit.jsp">
            <forward name="success" path="/listAccounts.do" redirect="true"/>
        </action>
    </action-mappings>

    <message-resources parameter="MessageResources"/>
</struts-config>
```

**Transformation Steps**:
1. Extract action paths → Map to `@RequestMapping` values (already done in Step 2)
2. Extract form beans → Converted to DTOs (already done in Step 3)
3. Extract forwards → Hardcoded return values in controllers
4. Extract message resources → Move to `src/main/resources/messages.properties`
5. **DELETE** struts-config.xml after migration

### 6.2 web.xml Analysis and Removal

#### Pattern: Extract Configuration from web.xml
```xml
<web-app>
    <servlet>
        <servlet-name>action</servlet-name>
        <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
        <init-param>
            <param-name>config</param-name>
            <param-value>/WEB-INF/struts-config.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>action</servlet-name>
        <url-pattern>*.do</url-pattern>
    </servlet-mapping>

    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>

    <error-page>
        <error-code>404</error-code>
        <location>/error.jsp</location>
    </error-page>
</web-app>
```

**Spring Boot Equivalent** (application.yml):
```yaml
server:
  port: 8080
  servlet:
    context-path: /  # Or /bank if needed
    session:
      timeout: 30m

spring:
  mvc:
    view:
      prefix: /templates/
      suffix: .html
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false  # For custom 404 handling
```

**Error Page Configuration** (Create ErrorController):
```java
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                model.addAttribute("errorMessage", "Page not found");
            } else if (statusCode == 500) {
                model.addAttribute("errorMessage", "Internal server error");
            }
        }

        return "error";
    }
}
```

**Welcome Page**: Create controller method
```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/accounts";  // Or your main page
    }
}
```

**DELETE** web.xml after migration (not needed in Spring Boot)

### 6.3 Application Configuration

#### Pattern: Create application.yml
**Create**: `src/main/resources/application.yml`

**Full Configuration Template**:
```yaml
spring:
  application:
    name: bank-application

  # Database Configuration
  datasource:
    url: jdbc:h2:mem:bankdb
    username: sa
    password:
    driver-class-name: org.h2.Driver

  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update  # create, create-drop, validate, update, none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect

  # H2 Console (Development only)
  h2:
    console:
      enabled: true
      path: /h2-console

  # Thymeleaf Configuration
  thymeleaf:
    cache: false  # Disable cache in development
    prefix: classpath:/templates/
    suffix: .html

  # Message Resources
  messages:
    basename: messages
    encoding: UTF-8

  # Server Configuration
server:
  port: 8080
  servlet:
    context-path: /
    session:
      timeout: 30m

  # Error handling
  error:
    include-message: always
    include-stacktrace: on_param  # Show stacktrace with ?trace=true

# Logging Configuration
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**Environment-Specific Configuration**:

**application-dev.yml** (Development):
```yaml
spring:
  jpa:
    show-sql: true
  h2:
    console:
      enabled: true
```

**application-prod.yml** (Production):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  h2:
    console:
      enabled: false
```

**Activate Profile**:
```bash
java -jar app.jar --spring.profiles.active=prod
```

---

## Step 7: Application Bootstrap

### 7.1 Create Main Application Class

#### Pattern: Spring Boot Entry Point
**Create**: Main application class with `@SpringBootApplication`

```java
package com.example;  // Use root package of your application

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}
```

**Component Scanning**: `@SpringBootApplication` auto-scans:
- Current package and all sub-packages
- Registers `@Controller`, `@Service`, `@Repository`, `@Component` beans

**Explicit Scanning** (if needed):
```java
@SpringBootApplication(scanBasePackages = {"com.example", "com.other"})
```

### 7.2 Application Properties Organization

**Package Structure Best Practices**:
```
com.example
├── BankApplication.java          # Main class
├── controller/                   # @Controller classes
├── service/                      # @Service classes
├── repository/                   # @Repository interfaces
├── entity/                       # @Entity classes
├── dto/                          # DTOs (forms)
├── config/                       # @Configuration classes
├── exception/                    # Custom exceptions
└── util/                         # Utility classes
```

---

## Step 8: Error Handling and Validation

### 8.1 Global Exception Handling

#### Pattern: Create @ControllerAdvice
**Create**: Global exception handler for all controllers

```java
package com.example.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex,
                                       RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/accounts";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred");
        model.addAttribute("technicalDetails", ex.getMessage());
        return "error";
    }
}
```

### 8.2 Custom Business Exceptions

#### Pattern: Create Domain-Specific Exceptions
```java
package com.example.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Account not found with ID: " + id);
    }
}

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(BigDecimal balance, BigDecimal amount) {
        super(String.format("Insufficient funds. Balance: %s, Required: %s",
            balance, amount));
    }
}
```

**Usage in Service**:
```java
@Service
public class AccountService {
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public void withdraw(Long id, BigDecimal amount) {
        Account account = getAccount(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(account.getBalance(), amount);
        }
        // ... perform withdrawal
    }
}
```

### 8.3 Validation Error Handling

#### Pattern: Display Validation Errors in Thymeleaf
**Controller**:
```java
@PostMapping("/save")
public String save(@Valid @ModelAttribute AccountDTO accountDTO,
                  BindingResult result,
                  RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        // Return to form, errors automatically available in model
        return "accounts/edit";
    }

    // ... save logic
    redirectAttributes.addFlashAttribute("success", "Account saved successfully");
    return "redirect:/accounts";
}
```

**Thymeleaf Template**:
```html
<form th:action="@{/accounts/save}" th:object="${accountDTO}" method="post">
    <!-- Global errors -->
    <div th:if="${#fields.hasErrors('global')}" class="alert alert-danger">
        <ul>
            <li th:each="err : ${#fields.errors('global')}" th:text="${err}"></li>
        </ul>
    </div>

    <!-- Field-specific errors -->
    <div>
        <label>Holder Name:</label>
        <input type="text" th:field="*{holderName}"
               th:classappend="${#fields.hasErrors('holderName')} ? 'is-invalid'"/>
        <span th:if="${#fields.hasErrors('holderName')}"
              th:errors="*{holderName}"
              class="error"></span>
    </div>

    <!-- Success message -->
    <div th:if="${success}" class="alert alert-success" th:text="${success}"></div>

    <button type="submit">Save</button>
</form>
```

---

## Step 9: Testing Strategy

### 9.1 Repository Tests

#### Pattern: Test Repository Layer
```java
package com.example.repository;

import com.example.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // Sets up in-memory database and JPA context
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testFindByAccountNumber() {
        // Given
        Account account = new Account();
        account.setAccountNumber("ACC001");
        account.setHolderName("Test User");
        account.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(account);

        // When
        Optional<Account> found = accountRepository.findByAccountNumber("ACC001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getHolderName()).isEqualTo("Test User");
    }
}
```

### 9.2 Service Tests

#### Pattern: Test Service Layer with Mocks
```java
package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void testDeposit() {
        // Given
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("1000.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        accountService.deposit(1L, new BigDecimal("500.00"));

        // Then
        verify(accountRepository).save(account);
        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        // Given
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("100.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // When/Then
        assertThatThrownBy(() -> accountService.withdraw(1L, new BigDecimal("500.00")))
            .isInstanceOf(InsufficientFundsException.class);
    }
}
```

### 9.3 Controller Tests

#### Pattern: Test Controller Layer
```java
package com.example.controller;

import com.example.dto.AccountDTO;
import com.example.entity.Account;
import com.example.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    public void testListAccounts() throws Exception {
        // Given
        Account account = new Account();
        account.setId(1L);
        account.setHolderName("Test User");

        when(accountService.getAllActiveAccounts()).thenReturn(Arrays.asList(account));

        // When/Then
        mockMvc.perform(get("/accounts"))
            .andExpect(status().isOk())
            .andExpect(view().name("accounts/list"))
            .andExpect(model().attributeExists("accounts"));
    }

    @Test
    public void testSaveAccountWithValidation() throws Exception {
        mockMvc.perform(post("/accounts/save")
                .param("holderName", "")  // Invalid - blank
                .param("balance", "100"))
            .andExpect(status().isOk())
            .andExpect(view().name("accounts/edit"))
            .andExpect(model().attributeHasFieldErrors("accountDTO", "holderName"));
    }
}
```

---

## Step 10: Migration Checklist

### 10.1 Pre-Migration Checklist
- [ ] Backup entire Struts application
- [ ] Document all custom configurations
- [ ] Identify all third-party integrations
- [ ] List all database tables and relationships
- [ ] Map all user-facing URLs
- [ ] Document business logic in Actions and DAOs

### 10.2 Migration Execution Checklist

#### Dependencies
- [ ] Add Spring Boot parent POM
- [ ] Add spring-boot-starter-web
- [ ] Add spring-boot-starter-data-jpa
- [ ] Add spring-boot-starter-thymeleaf
- [ ] Add spring-boot-starter-validation
- [ ] Add database driver dependency
- [ ] Remove all Struts dependencies
- [ ] Update Java version to 17+

#### Entities
- [ ] Create @Entity classes for all database tables
- [ ] Add JPA annotations (@Id, @Column, etc.)
- [ ] Add relationships (@OneToMany, @ManyToOne, etc.)
- [ ] Add lifecycle callbacks (@PrePersist, @PreUpdate)
- [ ] Test entity mappings

#### Repositories
- [ ] Create Repository interfaces extending JpaRepository
- [ ] Add derived query methods
- [ ] Add custom @Query methods for complex queries
- [ ] Add @Modifying queries for updates
- [ ] Test repository methods

#### Services
- [ ] Create @Service classes
- [ ] Add @Transactional annotations
- [ ] Migrate business logic from DAOs
- [ ] Implement exception handling
- [ ] Add validation logic
- [ ] Test service methods

#### DTOs
- [ ] Create DTO classes from ActionForms
- [ ] Change String fields to proper types
- [ ] Add Bean Validation annotations
- [ ] Remove reset() and validate() methods
- [ ] Test DTO validation

#### Controllers
- [ ] Create @Controller classes from Actions
- [ ] Add @RequestMapping annotations
- [ ] Add @GetMapping/@PostMapping methods
- [ ] Implement constructor injection
- [ ] Add model attributes
- [ ] Add redirect attributes
- [ ] Handle validation errors
- [ ] Test controller methods

#### Views
- [ ] Convert JSP files to Thymeleaf templates
- [ ] Replace Struts tags with Thymeleaf attributes
- [ ] Update form bindings
- [ ] Update URL expressions
- [ ] Extract CSS to static files
- [ ] Update resource references
- [ ] Test all views

#### Configuration
- [ ] Create application.yml
- [ ] Configure datasource
- [ ] Configure JPA settings
- [ ] Configure Thymeleaf
- [ ] Configure message resources
- [ ] Create main @SpringBootApplication class
- [ ] Delete web.xml
- [ ] Delete struts-config.xml
- [ ] Test configuration

#### Error Handling
- [ ] Create @ControllerAdvice for global exceptions
- [ ] Create custom exception classes
- [ ] Create error page templates
- [ ] Test error scenarios

#### Testing
- [ ] Write repository tests
- [ ] Write service tests
- [ ] Write controller tests
- [ ] Write integration tests
- [ ] Test all user flows end-to-end

### 10.3 Post-Migration Checklist
- [ ] All unit tests passing
- [ ] All integration tests passing
- [ ] Manual testing of all features complete
- [ ] Performance testing complete
- [ ] Security review complete
- [ ] Code review complete
- [ ] Documentation updated
- [ ] Deployment tested in staging environment
- [ ] Rollback plan documented

---

## Step 11: Common Patterns and Pitfalls

### 11.1 Common Migration Patterns

#### Pattern: Soft Delete
**Struts DAO**:
```java
public void delete(Long id) {
    String sql = "UPDATE accounts SET active = FALSE WHERE id = ?";
    // execute
}

public List<Account> findAll() {
    String sql = "SELECT * FROM accounts WHERE active = TRUE";
    // execute
}
```

**Spring Boot**:
```java
// Entity
@Entity
@Where(clause = "active = true")  // Hibernate annotation for global filter
public class Account {
    private Boolean active = true;
}

// Repository
@Modifying
@Query("UPDATE Account a SET a.active = false WHERE a.id = :id")
void softDelete(@Param("id") Long id);

// Or use @SQLDelete
@Entity
@SQLDelete(sql = "UPDATE accounts SET active = false WHERE id = ?")
public class Account { }
```

#### Pattern: Optimistic Locking
**Add to Entity**:
```java
@Entity
public class Account {
    @Version
    private Long version;  // Automatically managed by JPA
}
```

#### Pattern: Auditing
**Enable Auditing**:
```java
@Configuration
@EnableJpaAuditing
public class JpaConfig { }
```

**Entity with Audit Fields**:
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
```

### 11.2 Common Pitfalls

#### Pitfall: N+1 Query Problem
**Problem**: Lazy loading causes multiple queries
```java
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
    order.getCustomer().getName();  // Triggers separate query for each order
}
```

**Solution**: Use fetch joins
```java
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllWithCustomer();
```

#### Pitfall: Transaction Boundaries
**Problem**: @Modifying query outside transaction
```java
@Query("UPDATE Account SET balance = :balance WHERE id = :id")
void updateBalance(Long id, BigDecimal balance);  // Will fail
```

**Solution**: Add @Transactional to service method
```java
@Service
public class AccountService {
    @Transactional
    public void updateBalance(Long id, BigDecimal balance) {
        accountRepository.updateBalance(id, balance);
    }
}
```

#### Pitfall: DTOs vs Entities
**Problem**: Exposing entities in controllers
```java
@PostMapping("/save")
public String save(@ModelAttribute Account account) {  // BAD: entity
    // Problem: Client can modify any field, including ID
}
```

**Solution**: Always use DTOs
```java
@PostMapping("/save")
public String save(@Valid @ModelAttribute AccountDTO dto) {  // GOOD: DTO
    Account account = convertToEntity(dto);
    accountService.save(account);
}
```

---

## Step 12: Advanced Scenarios

### 12.1 REST API Support

**If Migrating to REST Instead of Thymeleaf**:

```java
@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        return accountService.getAccount(id)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody AccountDTO accountDTO) {
        Account saved = accountService.save(convertToEntity(accountDTO));
        return ResponseEntity
            .created(URI.create("/api/accounts/" + saved.getId()))
            .body(convertToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountDTO accountDTO) {
        return accountService.update(id, convertToEntity(accountDTO))
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 12.2 Security Migration

**If Struts App Has Custom Authentication**:

**Add Dependency**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Security Configuration**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/accounts")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Completion Criteria

The migration is complete when:

1. **All Struts dependencies removed** from pom.xml
2. **All Actions converted** to Spring Controllers
3. **All ActionForms converted** to DTOs with Bean Validation
4. **All DAOs converted** to JPA Repositories
5. **All JSPs converted** to Thymeleaf templates
6. **Configuration migrated** to application.yml
7. **Main application class created** with @SpringBootApplication
8. **Application starts successfully** with `mvn spring-boot:run`
9. **All features functional** - manual testing passes
10. **All tests passing** - unit, integration, and end-to-end tests
11. **No compilation errors or warnings**
12. **Performance acceptable** - no significant degradation
13. **Security validated** - no new vulnerabilities introduced

---

## Agent Execution Guidelines

When executing this migration as an automated agent:

1. **Always preserve business logic** - Never modify the core functionality
2. **Work incrementally** - Migrate one layer at a time (repository → service → controller → view)
3. **Test after each step** - Ensure each layer works before moving to the next
4. **Keep backup references** - Don't delete Struts files until migration is verified
5. **Log all transformations** - Document what was changed and why
6. **Handle edge cases** - Look for non-standard patterns and handle them appropriately
7. **Validate assumptions** - If uncertain about a pattern, mark for human review
8. **Preserve comments** - Keep business logic comments from original code
9. **Follow naming conventions** - Use Spring Boot standard naming patterns
10. **Generate tests** - Create basic tests for all new components

---

## End of Specification

This specification provides comprehensive, pattern-based guidance for migrating Apache Struts 1.x applications to Spring Boot. All transformations are generic and applicable to any Struts application following standard conventions.
