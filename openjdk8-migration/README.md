# OpenJDK 8 to 21 Migration Sample Project

This project demonstrates various APIs, features, and behaviors from OpenJDK 8 that require attention when migrating to OpenJDK 21. Each Java file contains examples of code patterns that may need modification, along with migration guidance.

## Migration Scenarios Covered

### Core API Changes
### 1. Deprecated APIs (`DeprecatedSecurityManagerExample.java`)
- **SecurityManager**: Deprecated in JDK 17, will be removed
- **Migration**: Replace with modern security frameworks or application-level controls

### 2. Removed Java EE APIs (`RemovedJavaEEAPIsExample.java`)
- **JAXB**: Removed from JDK 11+, requires external dependencies
- **JAF (Java Activation Framework)**: No longer included
- **Migration**: Add explicit Maven dependencies

### 3. Removed JavaScript Engine (`NashornJavaScriptEngineExample.java`)
- **Nashorn**: Deprecated in JDK 11, removed in JDK 15
- **Migration**: Use GraalVM JavaScript or Mozilla Rhino

### 4. Legacy Date/Time APIs (`DeprecatedDateTimeExample.java`)
- **Date/Calendar/SimpleDateFormat**: Error-prone legacy APIs
- **Migration**: Use java.time.* classes (LocalDateTime, DateTimeFormatter, etc.)

### 5. Tools.jar Dependencies (`RemovedToolsJarExample.java`)
- **tools.jar**: No longer needed in JDK 9+
- **Migration**: Use ProcessHandle, ToolProvider, or modern alternatives

### 6. Reflection Changes (`ChangedReflectionBehaviorExample.java`)
- **Strong Encapsulation**: JDK 9+ restricts access to internal APIs
- **Migration**: Use public APIs or --add-opens JVM arguments

### 7. Deprecated finalize() (`DeprecatedFinalizeExample.java`)
- **finalize()**: Deprecated for removal in JDK 9+
- **Migration**: Use try-with-resources, Cleaner API, or explicit cleanup

### 8. Garbage Collection Changes (`ChangedGarbageCollectionExample.java`)
- **Default GC**: Changed from Parallel GC (JDK 8) to G1GC (JDK 9+)
- **Migration**: Update GC tuning parameters

### 9. String Concatenation (`StringConcatenationChangesExample.java`)
- **Internal Changes**: JDK 9+ uses invokedynamic for better performance
- **Migration**: Generally no code changes needed

### Konveyor Ruleset-Based Examples
### 10. Removed Thread Methods (`RemovedThreadMethodsExample.java`)
- **Thread.stop(Throwable)** and **Thread.destroy()**: Removed in JDK 11+
- **Konveyor Rule**: `java-removals-00000`
- **Migration**: Use cooperative shutdown with interruption

### 11. sun.reflect.Reflection Deprecation (`SunReflectDeprecationExample.java`)
- **sun.reflect.Reflection** and **@CallerSensitive**: Deprecated in JDK 9+
- **Konveyor Rules**: `java-removals-00010`, `java-removals-00020`
- **Migration**: Use StackWalker API for caller detection

### 12. javax.security.auth.Policy Removal (`RemovedJavaxSecurityExample.java`)
- **javax.security.auth.Policy**: Removed in JDK 11+
- **Konveyor Rule**: `java-removals-00030`
- **Migration**: Use java.security.Policy with ProtectionDomain

### 13. CORBA Module Removal (`RemovedCORBAExample.java`)
- **javax.activity**, **javax.rmi**, **com.sun.corba**: Removed in JDK 11+
- **Konveyor Rule**: `removed-javaee-modules-00010`
- **Migration**: Use REST APIs, gRPC, or GlassFish CORBA implementation

### 14. Applet API Deprecation (`DeprecatedAppletAPIExample.java`)
- **java.applet.*** and **javax.swing.JApplet**: Deprecated in JDK 17+
- **Konveyor Rule**: `applet-api-deprecation-00000`
- **Migration**: Convert to standalone applications or web applications

### 15. Pack200 API Removal (`RemovedPack200Example.java`)
- **java.util.jar.Pack200**: Removed in JDK 14+
- **Konveyor Rule**: `removed-classes-00000`
- **Migration**: Use jlink, jpackage, or standard compression

### 16. Swing PLAF Deprecations (`SwingPLAFDeprecationExample.java`)
- **javax.swing.plaf.basic** internal classes: Deprecated in JDK 19+
- **Konveyor Rule**: `deprecation-00010`
- **Migration**: Use public Swing APIs and avoid PLAF internals

## Running the Examples

### Prerequisites
- JDK 8 (to see original behavior)
- Maven 3.6+

### Build and Run
```bash
# Build the project
mvn clean compile

# Run individual examples (Core API Changes)
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.DeprecatedSecurityManagerExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.RemovedJavaEEAPIsExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.NashornJavaScriptEngineExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.DeprecatedDateTimeExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.RemovedToolsJarExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.ChangedReflectionBehaviorExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.DeprecatedFinalizeExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.ChangedGarbageCollectionExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.StringConcatenationChangesExample"

# Run Konveyor Ruleset-Based Examples
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.RemovedThreadMethodsExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.SunReflectDeprecationExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.RemovedJavaxSecurityExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.RemovedCORBAExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.DeprecatedAppletAPIExample"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.RemovedPack200Example"
mvn exec:java -Dexec.mainClass="com.redhat.migration.jdk8to21.SwingPLAFDeprecationExample"
```
