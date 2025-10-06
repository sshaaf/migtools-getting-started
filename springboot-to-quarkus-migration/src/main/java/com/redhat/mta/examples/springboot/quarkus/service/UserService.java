package com.redhat.mta.examples.springboot.quarkus.service;

import com.redhat.mta.examples.springboot.quarkus.exception.ResourceNotFoundException;
import com.redhat.mta.examples.springboot.quarkus.exception.DuplicateResourceException;
import com.redhat.mta.examples.springboot.quarkus.model.User;
import com.redhat.mta.examples.springboot.quarkus.model.Role;
import com.redhat.mta.examples.springboot.quarkus.repository.UserRepository;
import com.redhat.mta.examples.springboot.quarkus.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * User Service demonstrating Spring patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Service with @ApplicationScoped
 * - Replace @Autowired with @Inject
 * - Replace @PreAuthorize with @RolesAllowed
 * - Replace @Cacheable/@CacheEvict with Quarkus Cache annotations
 * - Replace @Async with Quarkus async patterns
 * - Replace @Transactional with javax.transaction.Transactional
 */
@Service
@Transactional
@Validated
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Find all users with pagination support
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Page<User> findAll(Pageable pageable) {
        logger.debug("Finding all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    /**
     * Find user by ID with caching
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public User findById(@NotNull Long id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    /**
     * Find user by username
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Create new user
     */
    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@Valid User user) {
        logger.info("Creating new user: {}", user.getUsername());
        
        // Check for duplicates
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + user.getUsername());
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if no roles assigned
        if (user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));
            user.addRole(userRole);
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Update existing user
     */
    @CacheEvict(value = "users", key = "#user.id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #user.id == authentication.principal.id)")
    public User updateUser(@Valid User user) {
        logger.info("Updating user: {}", user.getId());
        
        User existingUser = findById(user.getId());
        
        // Check for username conflicts (excluding current user)
        userRepository.findByUsername(user.getUsername())
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(u -> {
                    throw new DuplicateResourceException("Username already exists: " + user.getUsername());
                });
        
        // Check for email conflicts (excluding current user)
        userRepository.findByEmail(user.getEmail())
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(u -> {
                    throw new DuplicateResourceException("Email already exists: " + user.getEmail());
                });

        // Update fields
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBio(user.getBio());
        
        // Update password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Delete user by ID
     */
    @CacheEvict(value = "users", key = "#id")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@NotNull Long id) {
        logger.info("Deleting user: {}", id);
        
        User user = findById(id);
        userRepository.delete(user);
        
        logger.info("User deleted successfully: {}", id);
    }

    /**
     * Activate/deactivate user
     */
    @CacheEvict(value = "users", key = "#id")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUserStatus(@NotNull Long id, boolean active) {
        logger.info("Updating user status: {} to {}", id, active);
        
        User user = findById(id);
        user.setActive(active);
        
        User updatedUser = userRepository.save(user);
        logger.info("User status updated successfully: {}", id);
        return updatedUser;
    }

    /**
     * Add role to user
     */
    @CacheEvict(value = "users", key = "#userId")
    @PreAuthorize("hasRole('ADMIN')")
    public User addRoleToUser(@NotNull Long userId, @NotNull Long roleId) {
        logger.info("Adding role {} to user {}", roleId, userId);
        
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
        
        user.addRole(role);
        
        User updatedUser = userRepository.save(user);
        logger.info("Role added successfully to user: {}", userId);
        return updatedUser;
    }

    /**
     * Remove role from user
     */
    @CacheEvict(value = "users", key = "#userId")
    @PreAuthorize("hasRole('ADMIN')")
    public User removeRoleFromUser(@NotNull Long userId, @NotNull Long roleId) {
        logger.info("Removing role {} from user {}", roleId, userId);
        
        User user = findById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
        
        user.removeRole(role);
        
        User updatedUser = userRepository.save(user);
        logger.info("Role removed successfully from user: {}", userId);
        return updatedUser;
    }

    /**
     * Find active users
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findActiveUsers() {
        logger.debug("Finding all active users");
        return userRepository.findByActive(true);
    }

    /**
     * Find users by role
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findUsersByRole(String roleName) {
        logger.debug("Finding users by role: {}", roleName);
        return userRepository.findByRoleName(roleName);
    }

    /**
     * Search users by name
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Page<User> searchUsers(String firstName, String lastName, String email, Boolean active, Pageable pageable) {
        logger.debug("Searching users with filters - firstName: {}, lastName: {}, email: {}, active: {}", 
                    firstName, lastName, email, active);
        return userRepository.findUsersWithFilters(firstName, lastName, email, active, pageable);
    }

    /**
     * Count active users
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public long countActiveUsers() {
        return userRepository.countByActive(true);
    }

    /**
     * Async method to send welcome email (example of async processing)
     */
    @Async
    public CompletableFuture<Void> sendWelcomeEmailAsync(Long userId) {
        logger.info("Sending welcome email asynchronously for user: {}", userId);
        
        try {
            User user = findById(userId);
            
            // Simulate email sending delay
            Thread.sleep(2000);
            
            logger.info("Welcome email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send welcome email for user: {}", userId, e);
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Clean up inactive users (scheduled method example)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public int cleanupInactiveUsers(int daysOld) {
        logger.info("Cleaning up inactive users older than {} days", daysOld);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int deletedCount = userRepository.deleteInactiveUsersOlderThan(cutoffDate);
        
        logger.info("Cleaned up {} inactive users", deletedCount);
        return deletedCount;
    }
}

