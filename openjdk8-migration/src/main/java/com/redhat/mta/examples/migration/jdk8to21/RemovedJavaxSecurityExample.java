package com.redhat.mta.examples.migration.jdk8to21;

import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.net.URL;

/**
 * Security policy manager using javax.security.auth.Policy.
 * javax.security.auth.Policy removed in JDK 11+
 */
public class RemovedJavaxSecurityExample {
    
    public PermissionCollection getPermissions(CodeSource codeSource) {
        Policy policy = Policy.getPolicy();
        ProtectionDomain domain = new ProtectionDomain(codeSource, null);
        return policy.getPermissions(domain);
    }
    
    public boolean checkPermission(CodeSource codeSource, java.security.Permission permission) {
        PermissionCollection permissions = getPermissions(codeSource);
        return permissions.implies(permission);
    }
    
    public void validateAccess(String location) throws SecurityException {
        try {
            URL url = new URL(location);
            CodeSource source = new CodeSource(url, (java.security.cert.Certificate[]) null);
            
            // Check basic permissions
            java.security.Permission readPerm = new java.io.FilePermission(location, "read");
            if (!checkPermission(source, readPerm)) {
                throw new SecurityException("Read access denied: " + location);
            }
        } catch (Exception e) {
            throw new SecurityException("Security validation failed", e);
        }
    }
    
    public static class CustomPolicy extends Policy {
        @Override
        public PermissionCollection getPermissions(CodeSource codesource) {
            return getPermissions(new ProtectionDomain(codesource, null));
        }
        
        @Override
        public PermissionCollection getPermissions(ProtectionDomain domain) {
            Permissions permissions = new Permissions();
            // Add custom permissions based on domain
            return permissions;
        }
    }
}
