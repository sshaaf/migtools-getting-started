package com.example.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
import javax.validation.Validator;

public class JavaxValidationController {
    
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







