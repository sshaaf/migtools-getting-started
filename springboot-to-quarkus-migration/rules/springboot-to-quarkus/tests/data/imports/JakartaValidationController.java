package com.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.Validator;

public class JakartaValidationController {
    
    @Inject
    Validator validator;
    
    public Response createUser(@Valid User user) {
        // validation logic
        return Response.ok().build();
    }
    
    public static class User {
        @NotNull
        @NotEmpty
        @Size(min = 3, max = 50)
        private String username;
        
        @Email
        private String email;
        
        // getters and setters
    }
}







