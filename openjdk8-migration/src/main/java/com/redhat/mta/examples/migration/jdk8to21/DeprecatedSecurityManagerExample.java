package com.redhat.mta.examples.migration.jdk8to21;

import java.io.FilePermission;
import java.security.Permission;
import java.util.PropertyPermission;
import java.util.Set;
import java.util.HashSet;

/**
 * Enterprise security framework using SecurityManager.
 * SecurityManager deprecated for removal in JDK 17+
 */
public class DeprecatedSecurityManagerExample {
    
    private static final Set<String> TRUSTED_CALLERS = new HashSet<>();
    
    static {
        TRUSTED_CALLERS.add("com.redhat.trusted");
        TRUSTED_CALLERS.add("com.enterprise.core");
        TRUSTED_CALLERS.add("java.");
        TRUSTED_CALLERS.add("javax.");
    }
    
    public void initializeSecurity() {
        System.setSecurityManager(new EnterpriseSecurityManager());
    }
    
    public void performSecureOperations() throws Exception {
        readConfigurationFile();
        writeAuditLog();
        updateSystemProperties();
        accessNetworkResource();
    }
    
    private void readConfigurationFile() throws Exception {
        java.io.File config = new java.io.File("enterprise.config");
        if (config.exists()) {
            // Read configuration
        }
    }
    
    private void writeAuditLog() throws Exception {
        java.io.File auditLog = new java.io.File("audit.log");
        auditLog.createNewFile();
    }
    
    private void updateSystemProperties() {
        System.setProperty("enterprise.mode", "secure");
        System.setProperty("audit.enabled", "true");
    }
    
    private void accessNetworkResource() {
        String hostname = System.getProperty("enterprise.hostname");
        // Network access logic
    }
    
    /**
     * Custom SecurityManager for enterprise security policies
     */
    private static class EnterpriseSecurityManager extends SecurityManager {
        
        @Override
        public void checkRead(String file) {
            if (isRestrictedFile(file)) {
                String caller = getCallerClass().getName();
                if (!isTrustedCaller(caller)) {
                    throw new SecurityException("Unauthorized file access: " + file);
                }
            }
        }
        
        @Override
        public void checkWrite(String file) {
            if (isSystemDirectory(file)) {
                throw new SecurityException("System directory write denied: " + file);
            }
            
            if (file.contains("audit") && !isAuditingModule()) {
                throw new SecurityException("Audit file access restricted");
            }
        }
        
        @Override
        public void checkPropertyAccess(String key) {
            if (isSecurityProperty(key)) {
                String caller = getCallerClass().getName();
                if (!isTrustedCaller(caller)) {
                    throw new SecurityException("Security property access denied: " + key);
                }
            }
        }
        
        @Override
        public void checkPermission(Permission perm) {
            if (perm instanceof FilePermission) {
                checkFilePermission((FilePermission) perm);
            } else if (perm instanceof PropertyPermission) {
                checkPropertyPermission((PropertyPermission) perm);
            }
        }
        
        @Override
        public void checkExit(int status) {
            String caller = getCallerClass().getName();
            if (!caller.startsWith("com.enterprise.shutdown")) {
                throw new SecurityException("Unauthorized system exit attempt");
            }
        }
        
        private boolean isRestrictedFile(String file) {
            return file.endsWith(".key") || 
                   file.endsWith(".secret") || 
                   file.contains("private") ||
                   file.contains("confidential");
        }
        
        private boolean isSystemDirectory(String file) {
            return file.startsWith("/system") || 
                   file.startsWith("/etc") ||
                   file.startsWith("C:\\Windows") ||
                   file.startsWith("C:\\System32");
        }
        
        private boolean isSecurityProperty(String key) {
            return key.startsWith("security.") ||
                   key.startsWith("java.security") ||
                   key.startsWith("enterprise.security");
        }
        
        private boolean isTrustedCaller(String caller) {
            return TRUSTED_CALLERS.stream().anyMatch(caller::startsWith);
        }
        
        private boolean isAuditingModule() {
            String caller = getCallerClass().getName();
            return caller.contains("audit") || caller.contains("logging");
        }
        
        private Class<?> getCallerClass() {
            return getClassContext()[2];
        }
        
        private void checkFilePermission(FilePermission fp) {
            if (fp.getActions().contains("execute")) {
                String caller = getCallerClass().getName();
                if (!isTrustedCaller(caller)) {
                    throw new SecurityException("Execution denied: " + fp.getName());
                }
            }
        }
        
        private void checkPropertyPermission(PropertyPermission pp) {
            if (pp.getActions().contains("write") && isSecurityProperty(pp.getName())) {
                throw new SecurityException("Security property modification denied");
            }
        }
    }
}
