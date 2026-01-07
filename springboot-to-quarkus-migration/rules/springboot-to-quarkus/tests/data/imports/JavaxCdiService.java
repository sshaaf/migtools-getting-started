package com.example.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class JavaxCdiService {
    
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







