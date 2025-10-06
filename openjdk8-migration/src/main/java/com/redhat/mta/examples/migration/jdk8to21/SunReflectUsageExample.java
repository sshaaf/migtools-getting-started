package com.redhat.mta.examples.migration.jdk8to21;

import sun.reflect.Reflection;
import sun.reflect.CallerSensitive;

/**
 * JDK 8 code using sun.reflect.Reflection internal APIs.
 * These are deprecated in JDK 9+ (Konveyor rules: java-removals-00010, java-removals-00020)
 */
public class SunReflectUsageExample {
    
    public static void main(String[] args) {
        SecurityFramework framework = new SecurityFramework();
        framework.performSecureOperation();
        
        CallerTracker tracker = new CallerTracker();
        tracker.trackMethodCalls();
        
        PrivilegedOperations ops = new PrivilegedOperations();
        ops.executePrivilegedCode();
    }
    
    /**
     * Security framework using sun.reflect.Reflection - deprecated in JDK 9+
     */
    static class SecurityFramework {
        
        @CallerSensitive  // This annotation is deprecated in JDK 9+
        public void performSecureOperation() {
            // Get the caller class using internal API - deprecated in JDK 9+
            Class<?> caller = Reflection.getCallerClass();
            
            if (!isAuthorizedCaller(caller)) {
                throw new SecurityException("Unauthorized access from: " + caller.getName());
            }
            
            // Perform the secure operation
            accessSensitiveResource();
        }
        
        private boolean isAuthorizedCaller(Class<?> callerClass) {
            String packageName = callerClass.getPackage().getName();
            return packageName.startsWith("com.redhat.mta.examples.migration") || 
                   packageName.startsWith("java.") ||
                   packageName.startsWith("javax.");
        }
        
        private void accessSensitiveResource() {
            // Simulate accessing sensitive resource
            System.setProperty("secure.data", "sensitive_value");
        }
    }
    
    /**
     * Method call tracker using sun.reflect.Reflection - deprecated in JDK 9+
     */
    static class CallerTracker {
        
        public void trackMethodCalls() {
            logMethodCall();
            performBusinessLogic();
            auditAccess();
        }
        
        @CallerSensitive  // This annotation is deprecated in JDK 9+
        private void logMethodCall() {
            // Use internal API to get caller information - deprecated in JDK 9+
            Class<?> caller = Reflection.getCallerClass();
            Class<?> callerCaller = Reflection.getCallerClass(2);
            
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            
            // Log the call chain
            recordAuditEntry(caller, callerCaller, methodName);
        }
        
        private void recordAuditEntry(Class<?> caller, Class<?> callerCaller, String method) {
            // Simulate audit logging
            String auditEntry = String.format("Method %s called by %s (initiated by %s)", 
                                             method, caller.getName(), 
                                             callerCaller != null ? callerCaller.getName() : "unknown");
        }
        
        private void performBusinessLogic() {
            // Simulate business logic
            calculateResults();
        }
        
        private void calculateResults() {
            // Another level of method calls to test caller detection
            Math.sqrt(42);
        }
        
        @CallerSensitive  // This annotation is deprecated in JDK 9+
        private void auditAccess() {
            Class<?> caller = Reflection.getCallerClass();
            
            // Check if caller is from trusted package
            if (caller.getPackage().getName().startsWith("sun.") ||
                caller.getPackage().getName().startsWith("com.sun.")) {
                // Skip auditing for internal JDK calls
                return;
            }
            
            // Record external access
            recordExternalAccess(caller);
        }
        
        private void recordExternalAccess(Class<?> caller) {
            // Simulate recording external access
        }
    }
    
    /**
     * Privileged operations using sun.reflect.Reflection - deprecated in JDK 9+
     */
    static class PrivilegedOperations {
        
        public void executePrivilegedCode() {
            // Different privilege levels based on caller
            restrictedOperation();
            adminOperation();
        }
        
        @CallerSensitive  // This annotation is deprecated in JDK 9+
        public void restrictedOperation() {
            Class<?> caller = Reflection.getCallerClass();
            
            // Only allow certain classes to call this method
            if (!isPrivilegedCaller(caller)) {
                throw new SecurityException("Access denied for: " + caller.getName());
            }
            
            // Perform restricted operation
            performRestrictedTask();
        }
        
        @CallerSensitive  // This annotation is deprecated in JDK 9+
        public void adminOperation() {
            Class<?> caller = Reflection.getCallerClass();
            
            // Check for admin privileges
            if (!hasAdminPrivileges(caller)) {
                throw new SecurityException("Admin access required, called by: " + caller.getName());
            }
            
            // Perform admin operation
            performAdminTask();
        }
        
        private boolean isPrivilegedCaller(Class<?> caller) {
            // Check if caller is from privileged package
            String className = caller.getName();
            return className.startsWith("com.redhat.mta.examples.migration.jdk8to21.SunReflectUsageExample") ||
                   className.startsWith("java.security.");
        }
        
        private boolean hasAdminPrivileges(Class<?> caller) {
            // Simulate admin privilege check
            return caller.getName().contains("Admin") || 
                   caller.getName().contains("Privileged") ||
                   isSystemCaller(caller);
        }
        
        private boolean isSystemCaller(Class<?> caller) {
            // Check if called from system/bootstrap classes
            ClassLoader loader = caller.getClassLoader();
            return loader == null; // Bootstrap classloader
        }
        
        private void performRestrictedTask() {
            // Simulate restricted operation
            System.getProperties().setProperty("restricted.flag", "true");
        }
        
        private void performAdminTask() {
            // Simulate admin operation
            System.getProperties().setProperty("admin.mode", "enabled");
        }
    }
}
