package com.example.security;

import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;

@ApplicationScoped
public class JakartaSecurityContext {
    
    @Inject
    SecurityContext securityContext;
    
    public boolean authenticate(String username, String password) {
        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        // authentication logic
        return securityContext.authenticate(null, null, null);
    }
}







