package com.redhat.mta.examples.springboot2.migration.controller;

import com.redhat.mta.examples.springboot2.migration.model.User;
import com.redhat.mta.examples.springboot2.migration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * User controller using deprecated Spring Boot 2 patterns.
 * 
 * Deprecated features:
 * - Field injection with @Autowired (deprecated in favor of constructor injection)
 * - javax.servlet imports (migrated to jakarta.servlet in Spring Boot 3)
 * - Custom Actuator endpoints using deprecated @Endpoint pattern
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user, 
                                         HttpServletRequest request, 
                                         HttpServletResponse response) {
        User createdUser = userService.createUser(user);
        response.setHeader("Location", "/api/users/" + createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
                                         @RequestBody User user,
                                         HttpServletRequest request) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Custom actuator endpoint using deprecated pattern
     */
    @Component
    @Endpoint(id = "users")
    public static class UserEndpoint {

        @Autowired
        private UserService userService;

        @ReadOperation
        public Map<String, Object> userStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userService.countUsers());
            stats.put("activeUsers", userService.countActiveUsers());
            return stats;
        }
    }
}
