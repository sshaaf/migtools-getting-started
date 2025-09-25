package com.redhat.mta.examples.springboot2.migration.service;

import com.redhat.mta.examples.springboot2.migration.model.User;
import com.redhat.mta.examples.springboot2.migration.repository.UserRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * User service using deprecated Spring Boot 2 patterns.
 * 
 * Deprecated features:
 * - @HystrixCommand (deprecated in favor of Spring Cloud Circuit Breaker)
 * - Field injection with @Autowired
 * - Optional.orElse(null) anti-pattern
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @HystrixCommand(fallbackMethod = "findAllUsersFallback")
    @Cacheable("users")
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @HystrixCommand(fallbackMethod = "findUserByIdFallback")
    @Cacheable(value = "user", key = "#id")
    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null); // Anti-pattern: should handle Optional properly
    }

    @HystrixCommand(fallbackMethod = "createUserFallback")
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email already exists");
        }
        return userRepository.save(user);
    }

    @HystrixCommand(fallbackMethod = "updateUserFallback")
    @CacheEvict(value = {"user", "users"}, allEntries = true)
    public User updateUser(Long id, User userUpdate) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userUpdate.getName());
            user.setEmail(userUpdate.getEmail());
            user.setActive(userUpdate.isActive());
            return userRepository.save(user);
        }
        return null;
    }

    @HystrixCommand(fallbackMethod = "deleteUserFallback")
    @CacheEvict(value = {"user", "users"}, allEntries = true)
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    // Hystrix fallback methods
    public List<User> findAllUsersFallback() {
        return new ArrayList<>();
    }

    public User findUserByIdFallback(Long id) {
        return null;
    }

    public User createUserFallback(User user) {
        throw new RuntimeException("User creation service is currently unavailable");
    }

    public User updateUserFallback(Long id, User user) {
        return null;
    }

    public boolean deleteUserFallback(Long id) {
        return false;
    }
}
