package com.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class JakartaCdiService {
    
    @Inject
    Event<UserCreatedEvent> userCreatedEvent;
    
    @Produces
    @RequestScoped
    public UserContext createUserContext() {
        return new UserContext();
    }
    
    public void onUserCreated(@Observes UserCreatedEvent event) {
        // handle event
    }
}







