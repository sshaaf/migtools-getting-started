# Struts 1.x to Spring Boot Migration Rules

This ruleset provides comprehensive Kantra rules for migrating Apache Struts 1.x applications to Spring Boot 3.x.

## Overview

Struts 1.x is a legacy MVC framework that reached end-of-life and has known security vulnerabilities. This ruleset helps automate the migration to modern Spring Boot applications.

## Rule Files

| File | Description | Effort |
|------|-------------|--------|
| `00-struts-dependencies-to-springboot.yaml` | Maven dependency migrations | Medium |
| `01-struts-action-to-springboot-controller.yaml` | Action → @Controller conversion | High |
| `02-struts-actionform-to-springboot-dto.yaml` | ActionForm → DTO with Bean Validation | Medium |
| `03-struts-config-to-springboot.yaml` | Configuration file migrations | Medium |
| `04-struts-jsp-to-thymeleaf.yaml` | JSP taglibs → Thymeleaf templates | High |
| `05-struts-dao-to-springboot-repository.yaml` | JDBC DAO → Spring Data JPA | High |

## Migration Mapping

### Architecture Changes

| Struts 1.x | Spring Boot 3.x |
|------------|-----------------|
| `Action` | `@Controller` |
| `ActionForm` | DTO with Bean Validation |
| `ActionForward` | Return view name String |
| `ActionMapping` | `@RequestMapping` / `@GetMapping` |
| `ActionMessages` | `RedirectAttributes` flash attributes |
| `ActionErrors` | `BindingResult` |
| `struts-config.xml` | Java annotations + `application.properties` |
| `web.xml` | `@SpringBootApplication` (auto-configured) |
| JSP + Struts taglibs | Thymeleaf templates |
| JDBC DAO | Spring Data JPA Repository |

### Dependency Replacements

| Struts Dependency | Spring Boot Starter |
|-------------------|---------------------|
| `struts-core` | `spring-boot-starter-web` |
| `struts-taglib` | `spring-boot-starter-thymeleaf` |
| `struts-tiles` | `thymeleaf-layout-dialect` |
| `commons-dbcp` | HikariCP (included in JPA starter) |
| N/A | `spring-boot-starter-data-jpa` |
| N/A | `spring-boot-starter-validation` |

### Annotation Mappings

| Struts | Spring |
|--------|--------|
| `extends Action` | `@Controller` |
| `mapping.findForward("success")` | `return "viewName";` |
| `mapping.findForward("redirect")` | `return "redirect:/path";` |
| `request.setAttribute()` | `model.addAttribute()` |
| `ActionForm.validate()` | `@Valid` + Bean Validation |
| `saveMessages()` | `redirectAttributes.addFlashAttribute()` |

## Usage

### Run Analysis with Kantra

```bash
kantra analyze \
  --input /path/to/struts-app \
  --output /path/to/output \
  --rules /path/to/struts-to-springboot/ \
  --target springboot
```

### Example Migration Steps

1. **Update pom.xml** - Add Spring Boot parent and starters
2. **Create main class** - Add `@SpringBootApplication` entry point
3. **Convert Actions to Controllers** - Replace `execute()` with `@GetMapping`/`@PostMapping` methods
4. **Convert ActionForms to DTOs** - Add Bean Validation annotations
5. **Migrate views** - Convert JSP files to Thymeleaf templates
6. **Replace DAO layer** - Implement Spring Data JPA repositories
7. **Delete configuration files** - Remove `struts-config.xml`, `web.xml`
8. **Configure application.properties** - Add database, view, and message configuration

## Labels

All rules use the following labels:
- `konveyor.io/source=struts` - Source technology
- `konveyor.io/target=springboot` - Target technology

## Categories

- **mandatory** - Changes required for migration
- **optional** - Recommended improvements

## Links

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring MVC Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)
- [Thymeleaf Tutorial](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Bean Validation Documentation](https://jakarta.ee/specifications/bean-validation/)

