package com.example.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.PermitAll;

@ApplicationScoped
public class JavaxAnnotationService {
    
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







