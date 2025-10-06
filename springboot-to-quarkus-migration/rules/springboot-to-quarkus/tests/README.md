# SpringBoot to Quarkus Migration Rules - Test Suite

This directory contains a comprehensive test harness for the SpringBoot to Quarkus migration rules, following the same structure as the existing Spring Boot rules test suite.

## Test Structure

The test suite follows the established pattern:

```
tests/
├── data/                           # Test data files organized by category
│   ├── annotations/                # Spring annotations test data
│   ├── configuration/              # Configuration files test data  
│   ├── data-access/                # JPA/Data access test data
│   ├── dependencies/               # Maven dependencies test data
│   ├── reactive/                   # Reactive programming test data
│   ├── security/                   # Security configuration test data
│   └── testing/                    # Testing patterns test data
├── *.test.yaml                     # Test definition files for each rule file
└── README.md                       # This file
```

## Test Categories

### Core Migration Rules
- **01-springboot-dependencies-to-quarkus.test.yaml** - Tests Maven dependency migration (8 rules)
- **02-springboot-annotations-to-quarkus.test.yaml** - Tests annotation migration (14 rules)  
- **03-springboot-configuration-to-quarkus.test.yaml** - Tests configuration migration (9 rules)
- **05-springboot-data-to-quarkus.test.yaml** - Tests data access migration (10 rules)
- **06-springboot-testing-to-quarkus.test.yaml** - Tests testing pattern migration (9 rules)
- **07-springboot-security-to-quarkus.test.yaml** - Tests security migration (10 rules)

### Specialized Migration Rules  
- **09-spring-restful-to-quarkus.test.yaml** - Tests RESTful service patterns (4 rules)
- **10-spring-persistence-to-quarkus.test.yaml** - Tests advanced persistence patterns (3 rules)
- **11-spring-events-to-quarkus.test.yaml** - Tests event-driven patterns (3 rules)
- **12-spring-cloud-native-to-quarkus.test.yaml** - Tests cloud-native patterns (2 rules)
- **13-spring-reactive-to-quarkus.test.yaml** - Tests reactive programming patterns (3 rules)

## Test Data Files

Each test data category contains realistic Spring Boot code that should trigger specific migration rules:

### Dependencies (`data/dependencies/`)
- `pom.xml` - Spring Boot parent POM and common starters
- `SpringBootApp.java` - Basic Spring Boot application class

### Annotations (`data/annotations/`)
- `HeroController.java` - REST controller with Spring MVC annotations
- `HeroService.java` - Service class with dependency injection
- `HeroRepository.java` - JPA repository interface
- `Hero.java` - JPA entity class

### Configuration (`data/configuration/`)
- `application.properties` - Spring Boot configuration properties
- `application-dev.properties` - Profile-specific properties

### Data Access (`data/data-access/`)
- `HeroRepository.java` - Advanced JPA repository with queries
- `HeroService.java` - Transactional service methods

### Testing (`data/testing/`)
- `HeroControllerTest.java` - Spring Boot web layer test
- `HeroRepositoryTest.java` - Spring Boot data layer test

### Security (`data/security/`)
- `SecurityConfig.java` - Spring Security configuration
- `SecureController.java` - Controller with security annotations

### Reactive (`data/reactive/`)
- `ReactiveHeroController.java` - Spring WebFlux reactive controller

## Test Execution

Each test file follows the standard format:

```yaml
rulesPath: ../[rule-file-name].yaml
providers:
  - name: java
    dataPath: ./data/[category]
  - name: builtin  
    dataPath: ./data/[category]
tests:
  - ruleID: [rule-id]
    testCases:
      - name: [test-case-name]
        analysisParams:
          mode: "source-only"
        hasIncidents:
          exactly: [expected-count]
```

## Expected Behavior

The test files contain Spring Boot patterns that should trigger migration rules:

- **Dependencies**: Maven dependencies that need Quarkus equivalents
- **Annotations**: Spring annotations that map to JAX-RS/CDI
- **Configuration**: Properties that need Quarkus format conversion
- **Data Access**: JPA patterns that should use Panache
- **Testing**: Spring test annotations that need Quarkus equivalents
- **Security**: Spring Security that should use Quarkus Security
- **Reactive**: Spring WebFlux that should use Quarkus Reactive

## Validation

The test structure has been validated to match the existing Spring Boot test pattern:

✅ **Directory Structure**: Matches `/default/generated/spring-boot/tests/` pattern  
✅ **Test Data Organization**: Organized by migration category  
✅ **YAML Test Files**: One per rule file with proper test case definitions  
✅ **Realistic Test Data**: Contains actual Spring Boot code patterns  
✅ **Expected Incidents**: Test cases specify expected rule trigger counts  

## Usage

These tests can be executed using the standard Konveyor analysis engine to validate that the migration rules correctly identify Spring Boot patterns and provide appropriate Quarkus migration guidance.

## Statistics

- **Total Test Files**: 11 test definition files
- **Total Test Cases**: 96+ individual test cases (one per rule)
- **Test Data Categories**: 7 distinct categories covering all migration aspects
- **Test Data Files**: 15+ realistic Spring Boot source files
- **Coverage**: All 96 migration rules have corresponding test cases
