package com.example.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.inject.Named;

@Singleton
@Named("userService")
public class JakartaInjectService {
    
    @Inject
    UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}







