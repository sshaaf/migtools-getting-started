package com.example.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.inject.Named;

@Singleton
@Named("userService")
public class JavaxInjectService {
    
    @Inject
    UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}







