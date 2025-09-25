# GitHub Actions CI/CD

This directory contains GitHub Actions workflows for continuous integration and deployment.

## 📋 Workflows Overview

### 🚀 Main CI Workflows

#### 1. `ci-main.yml` - Main Project (OpenJDK 17)
- **Triggers**: Push/PR to main branches
- **Purpose**: Compiles all modules except `openjdk8-migration` using OpenJDK 17
- **Modules**: `coolstore`, `fuse2camel`, `springboot2-migration`, `hotrod-repair`
- **Steps**:
  - ✅ Checkout code
  - ✅ Setup OpenJDK 17
  - ✅ Cache Maven dependencies
  - ✅ Compile all main modules
  - ✅ Run tests
  - ✅ Package artifacts
  - ✅ Upload build artifacts

#### 2. `ci-openjdk8-migration.yml` - OpenJDK 8 Migration Module
- **Triggers**: Push/PR affecting `openjdk8-migration/` or related files
- **Purpose**: Validates the OpenJDK 8 migration examples compile with JDK 8
- **Steps**:
  - ✅ Checkout code
  - ✅ Setup OpenJDK 8
  - ✅ Enable `openjdk8-migration` module temporarily
  - ✅ Compile with JDK 8 (shows migration issues)
  - ✅ Cross-compile check with JDK 17
  - ✅ Upload migration artifacts

### 🔍 Validation Workflows

#### 3. `ci-validate.yml` - Project Structure Validation
- **Triggers**: All pushes and PRs
- **Purpose**: Validates project structure and file integrity
- **Checks**:
  - ✅ Required files exist (`pom.xml`, `.gitignore`, `README.md`)
  - ✅ Module directories and POMs exist
  - ✅ XML syntax validation for all POM files
  - ✅ Docker Compose YAML validation
  - ✅ GitHub Actions workflow YAML validation

#### 4. `ci-combined-status.yml` - PR Status Management
- **Triggers**: Pull requests only
- **Purpose**: Provides combined status check for all CI workflows
- **Features**:
  - ⏳ Waits for all CI checks to complete
  - 📊 Posts status table in PR comments
  - ✅ Provides single pass/fail status for branch protection

## 🛡️ Branch Protection Setup

To ensure both compilation checks pass before merging PRs, configure branch protection rules:

### Required Status Checks
Add these checks as **required** in your branch protection settings:

1. `Compile Main Project with OpenJDK 17`
2. `Compile OpenJDK 8 Migration Module with OpenJDK 8`
3. `Validate Project Structure and Dependencies`

### Branch Protection Configuration
```yaml
# Example branch protection settings
branches:
  main:
    protection:
      required_status_checks:
        strict: true
        contexts:
          - "Compile Main Project with OpenJDK 17"
          - "Compile OpenJDK 8 Migration Module with OpenJDK 8"
          - "Validate Project Structure and Dependencies"
      enforce_admins: true
      required_pull_request_reviews:
        required_approving_review_count: 1
```

## 🔧 Workflow Behavior

### For Regular Modules (JDK 17)
```bash
# Compiles these modules successfully:
mvn clean compile -pl '!openjdk8-migration'
```
- ✅ `coolstore` - JBoss EAP 7 legacy application
- ✅ `fuse2camel` - Camel migration examples  
- ✅ `springboot2-migration` - Spring Boot 2.x deprecated APIs
- ✅ `hotrod-repair` - Java EE 7 patterns for EAP 8 migration

### For OpenJDK 8 Migration (JDK 8)
```bash
# Compiles migration examples with JDK 8:
mvn clean compile -pl openjdk8-migration
```
- ⚠️ **Expected behavior**: Some compilation failures are **intentional**
- 🎯 **Purpose**: Demonstrates APIs that don't exist in JDK 8
- 📊 **Analysis**: Shows what needs to be migrated from JDK 8 → JDK 21

## 🚨 Expected Compilation Failures

The `openjdk8-migration` module **intentionally** contains code that fails to compile with certain JDK versions:

### JDK 8 Compilation Issues (Expected)
- ❌ `sun.reflect.*` classes (internal APIs)
- ❌ `StackWalker` (JDK 9+ feature)
- ❌ `ProcessHandle` (JDK 9+ feature)

### JDK 21 Compilation Issues (Expected)
- ❌ `Thread.stop()` method removed
- ❌ `Pack200` API removed
- ❌ CORBA APIs removed
- ❌ `sun.reflect.Reflection` moved/removed

## 📊 Artifacts

### Build Artifacts
- **Main Project**: `main-project-artifacts-jdk17`
  - Contains: `*.jar`, `*.war` files from main modules
  - Retention: 7 days

- **OpenJDK 8 Migration**: `openjdk8-migration-artifacts-jdk8`
  - Contains: Compiled migration examples (when successful)
  - Retention: 7 days

## 🔄 Workflow Triggers

### Push Triggers
- `main`, `master`, `develop` branches
- All workflows run on push to these branches

### PR Triggers  
- **Main CI**: Runs for all PRs
- **OpenJDK 8 Migration**: Runs only when `openjdk8-migration/` files change
- **Combined Status**: Runs for all PRs, provides status summary

### Path-based Triggers
```yaml
paths:
  - 'openjdk8-migration/**'
  - 'pom.xml'
  - '.github/workflows/ci-openjdk8-migration.yml'
```

## 🛠️ Local Testing

### Test Main Project Compilation
```bash
# Test with OpenJDK 17
mvn clean compile -pl '!openjdk8-migration'
```

### Test OpenJDK 8 Migration
```bash
# Enable the module first
sed -i 's|<!-- <module>openjdk8-migration</module> -->|<module>openjdk8-migration</module>|g' pom.xml

# Test with OpenJDK 8
mvn clean compile -pl openjdk8-migration
```

### Validate Project Structure
```bash
# Check XML syntax
xmllint --noout pom.xml
xmllint --noout */pom.xml

# Check YAML syntax (if Python available)
python3 -c "import yaml; yaml.safe_load(open('docker-compose.yml'))"
```

## 🎯 Migration Analysis

The CI workflows help identify migration issues by:

1. **Showing JDK 8 compatibility** - What compiles with JDK 8
2. **Revealing JDK 21 changes** - What fails when moving to JDK 21
3. **Documenting API differences** - Through compilation errors
4. **Validating migration examples** - Ensuring examples demonstrate real issues

This approach provides **concrete evidence** of what needs to be migrated when moving from OpenJDK 8 to OpenJDK 21! 🚀
