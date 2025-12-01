# Quarkus 3.28.2 Migration Rule Updates

This document summarizes the changes made to update the Spring Boot to Quarkus migration rules from targeting Quarkus 2.x to Quarkus 3.28.2.

## Key Changes Made

### 1. New BOM Detection Rules (00-springboot-bom-to-quarkus.yaml)

**NEW FILE**: Added dedicated rules to ensure proper Quarkus BOM and Maven plugin setup:

- **BOM Detection Rule**: Detects when Spring Boot dependencies are present but Quarkus BOM is missing
- **Maven Plugin Rule**: Ensures Quarkus Maven plugin is added when migrating from Spring Boot
- **Comprehensive Coverage**: Detects multiple Spring Boot starters (web, data-jpa, security, actuator, etc.)
- **Negative Conditions**: Uses `not:` conditions to avoid triggering when Quarkus components are already present

### 2. Dependency Updates (01-springboot-dependencies-to-quarkus.yaml)

- **Quarkus BOM Version**: Updated to explicitly reference version `3.28.2`
- **Added Quarkus Version Property**: Added example of setting `quarkus.platform.version=3.28.2`
- **REST Extension**: Changed from `quarkus-resteasy-reactive-jackson` to `quarkus-rest-jackson`
- **Maven Plugin**: Updated documentation to reflect `io.quarkus.platform` groupId
- **Guide Links**: Updated RESTEasy guide links to point to the new REST guide

### 3. Jakarta EE Namespace Updates

Updated all `javax.*` references to `jakarta.*` to align with Quarkus 3.x Jakarta EE support:

- **Persistence**: `javax.persistence.*` → `jakarta.persistence.*`
- **Transactions**: `javax.transaction.*` → `jakarta.transaction.*`
- **Validation**: `javax.validation.*` → `jakarta.validation.*`

### 4. Configuration Updates (03-springboot-configuration-to-quarkus.yaml)

- **Metrics**: Updated from `quarkus.smallrye-metrics.extensions.enabled=true` to `quarkus.micrometer.enabled=true`

### 5. Extension Name Updates

- **Reactive REST**: Updated `quarkus-resteasy-reactive` to `quarkus-rest`
- **Guide References**: Updated all RESTEasy guide references to point to the new REST guides

### 6. Documentation Updates

- **Ruleset Description**: Updated to specify "Quarkus 3.28.2" as the target
- **Guide Links**: Updated various guide links to point to current Quarkus documentation

## Files Modified

1. **`00-springboot-bom-to-quarkus.yaml`** - **NEW**: Dedicated BOM and Maven plugin detection rules
2. `01-springboot-dependencies-to-quarkus.yaml` - Dependency and BOM updates
3. **`14-thymeleaf-to-qute.yaml`** - **NEW**: Comprehensive Thymeleaf to Qute migration rules
3. `02-springboot-annotations-to-quarkus.yaml` - REST terminology updates
4. `03-springboot-configuration-to-quarkus.yaml` - Metrics configuration updates
5. `04-springboot-web-to-quarkus.yaml` - Jakarta validation namespace
6. `05-springboot-data-to-quarkus.yaml` - Jakarta persistence and transaction namespaces
7. `09-spring-restful-to-quarkus.yaml` - REST guide link updates
8. `10-spring-persistence-to-quarkus.yaml` - Jakarta persistence namespace
9. `13-spring-reactive-to-quarkus.yaml` - REST extension name update
10. `ruleset.yaml` - Updated description to specify Quarkus 3.28.2

## New BOM Detection Rules

The new `00-springboot-bom-to-quarkus.yaml` file addresses a critical migration gap:

### Rule 1: BOM Detection (`springboot-bom-to-quarkus-00001`)
- **Triggers**: When any Spring Boot starter is detected AND Quarkus BOM is missing
- **Action**: Instructs users to add Quarkus BOM to `<dependencyManagement>`
- **Coverage**: Detects 8 common Spring Boot starters
- **Priority**: MANDATORY (effort: 3)

### Rule 2: Maven Plugin Detection (`springboot-bom-to-quarkus-00002`)
- **Triggers**: When Spring Boot dependencies are present AND Quarkus Maven plugin is missing
- **Action**: Instructs users to add Quarkus Maven plugin to `<build><plugins>`
- **Features**: Explains dev mode, build goals, and native compilation support
- **Priority**: MANDATORY (effort: 2)

## Compatibility Notes

- **Maven Version**: Quarkus 3.x requires Maven 3.8.6 or later
- **Jakarta EE**: All javax.* packages have been migrated to jakarta.*
- **REST Extensions**: The new `quarkus-rest` extension replaces the older RESTEasy extensions
- **Metrics**: Micrometer is now the preferred metrics solution over SmallRye Metrics
- **BOM Management**: Proper BOM setup is now explicitly enforced through dedicated rules

## Testing

- Added test files for the new BOM detection rules
- Test data includes both positive and negative cases
- All existing test files should continue to work with these updates

## New Thymeleaf to Qute Migration Rules

The new `14-thymeleaf-to-qute.yaml` file provides comprehensive migration guidance for templating engines:

### Rule Coverage (9 rules total):
1. **Dependency Migration** (`thymeleaf-to-qute-00001`) - Replace `spring-boot-starter-thymeleaf` with `quarkus-qute`
2. **Controller Migration** (`thymeleaf-to-qute-00002`) - Convert `@Controller` + `Model` to `@Path` + `TemplateInstance`
3. **Model Binding** (`thymeleaf-to-qute-00003`) - Replace Spring `Model` with Qute data binding
4. **Template Syntax** (`thymeleaf-to-qute-00004`) - Convert Thymeleaf expressions to Qute syntax
5. **Configuration** (`thymeleaf-to-qute-00005`) - Replace Thymeleaf properties with Qute configuration
6. **Fragment System** (`thymeleaf-to-qute-00006`) - Convert fragments to Qute includes
7. **Security Integration** (`thymeleaf-to-qute-00007`) - Replace Spring Security expressions with Quarkus Security
8. **Form Handling** (`thymeleaf-to-qute-00008`) - Convert form binding to Quarkus form handling
9. **Internationalization** (`thymeleaf-to-qute-00009`) - Replace Thymeleaf i18n with Quarkus localization

### Key Migration Patterns:
- **Syntax Changes**: `th:text="${var}"` → `{var}`, `th:if="${condition}"` → `{#if condition}`
- **Controller Pattern**: `Model.addAttribute()` → `TemplateInstance.data()`
- **Security**: `sec:authorize` → `{#if identity.hasRole()}`
- **Forms**: `th:field` → Manual form handling with validation
- **i18n**: `#{message.key}` → `{msg:message_key}`

## New javax to jakarta Import Migration Rules

The new `15-javax-to-jakarta-imports.yaml` file provides comprehensive migration rules for all javax to jakarta namespace changes required in Quarkus 3.x:

### Rule Coverage (10 rules total):
1. **JAX-RS Imports** (`javax-to-jakarta-imports-00001`) - `javax.ws.rs.*` → `jakarta.ws.rs.*`
2. **Dependency Injection** (`javax-to-jakarta-imports-00002`) - `javax.inject.*` → `jakarta.inject.*`
3. **CDI Imports** (`javax-to-jakarta-imports-00003`) - `javax.enterprise.*` → `jakarta.enterprise.*`
4. **JPA Imports** (`javax-to-jakarta-imports-00004`) - `javax.persistence.*` → `jakarta.persistence.*`
5. **Transaction Imports** (`javax-to-jakarta-imports-00005`) - `javax.transaction.*` → `jakarta.transaction.*`
6. **Validation Imports** (`javax-to-jakarta-imports-00006`) - `javax.validation.*` → `jakarta.validation.*`
7. **Servlet Imports** (`javax-to-jakarta-imports-00007`) - `javax.servlet.*` → `jakarta.servlet.*`
8. **JSON Processing** (`javax-to-jakarta-imports-00008`) - `javax.json.*` → `jakarta.json.*`
9. **Security Imports** (`javax-to-jakarta-imports-00009`) - `javax.security.*` → `jakarta.security.*`
10. **Common Annotations** (`javax-to-jakarta-imports-00010`) - `javax.annotation.*` → `jakarta.annotation.*`

### Key Features:
- **Comprehensive Coverage**: Covers all major javax packages used in enterprise Java applications
- **Import Location Targeting**: Uses `location: IMPORT` to specifically target import statements
- **Pattern Matching**: Uses wildcard patterns (`javax.ws.rs.*`) to catch all subpackages
- **Detailed Examples**: Each rule provides before/after code examples showing the exact import changes needed
- **Test Coverage**: Includes comprehensive test data with both javax (should trigger) and jakarta (should not trigger) examples

### Updated Existing Rules:
All existing rules have been updated to show correct `jakarta.*` imports in their code examples:
- **Web Layer Rules**: Updated JAX-RS import examples to use `jakarta.ws.rs.*`
- **Annotation Rules**: Updated CDI and validation import examples to use `jakarta.*`
- **Thymeleaf to Qute Rules**: Updated controller examples to show correct Jakarta imports
- **Data Access Rules**: Updated JPA and transaction import examples

The migration rules now properly target Quarkus 3.28.2 and include comprehensive BOM detection, templating engine migration, and complete javax to jakarta namespace migration to ensure complete Spring Boot to Quarkus migration coverage!
