package com.example.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.PermitAll;

@ApplicationScoped
public class JakartaAnnotationService {
    
    @PostConstruct
    public void init() {
        // initialization logic
    }
    
    @PreDestroy
    public void cleanup() {
        // cleanup logic
    }
    
    @RolesAllowed("admin")
    public void adminOnlyMethod() {
        // admin only logic
    }
    
    @PermitAll
    public void publicMethod() {
        // public logic
    }
}







