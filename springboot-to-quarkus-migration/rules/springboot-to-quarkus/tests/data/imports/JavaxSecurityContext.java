package com.example.security;

import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.credential.UsernamePasswordCredential;

@ApplicationScoped
public class JavaxSecurityContext {
    
    @Inject
    SecurityContext securityContext;
    
    public boolean authenticate(String username, String password) {
        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        // authentication logic
        return securityContext.authenticate(null, null, null);
    }
}







