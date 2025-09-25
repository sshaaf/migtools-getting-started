package com.redhat.mta.examples.spring.framework.service;

import com.redhat.mta.examples.spring.framework.model.User;
import com.redhat.mta.examples.spring.framework.model.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Spring Service using deprecated patterns and approaches.
 * 
 * Deprecated patterns for Spring Framework 6 migration:
 * - Field injection with @Autowired (should use constructor injection)
 * - javax.annotation usage (should be jakarta.annotation in Spring 6)
 * - Legacy transaction management patterns
 * - Deprecated caching approaches
 * - Legacy async processing patterns
 * - Deprecated scheduling configurations
 */
@Service
@Transactional(readOnly = true)  // Deprecated: Class-level transaction for read operations
public class UserService {

    // Deprecated: Field injection - should use constructor injection in Spring 6
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Deprecated: Field injection for caching manager
    @Autowired
    private org.springframework.cache.CacheManager cacheManager;

    /**
     * Deprecated: javax.annotation.PostConstruct
     * Spring 6: Should use jakarta.annotation.PostConstruct
     */
    @PostConstruct
    public void initializeService() {
        // Deprecated: Manual service initialization
        loadDefaultConfiguration();
        validateDependencies();
        initializeCache();
        
        // Deprecated: Direct logging without proper framework
        // Should use proper logging framework in Spring 6
    }

    /**
     * Deprecated: javax.annotation.PreDestroy
     * Spring 6: Should use jakarta.annotation.PreDestroy
     */
    @PreDestroy
    public void cleanupService() {
        // Deprecated: Manual cleanup logic
        clearCaches();
        closeConnections();
        // Manual resource cleanup patterns
    }

    /**
     * Deprecated: Legacy transaction management with complex configuration
     * Spring 6: Should use simpler transaction patterns
     */
    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,
        rollbackFor = {Exception.class},
        noRollbackFor = {IllegalArgumentException.class}
    )
    public User createUser(User user) {
        // Deprecated: Manual validation in service layer
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        // Deprecated: Manual duplicate check
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("User with email already exists: " + user.getEmail());
        }
        
        // Deprecated: Manual timestamp setting
        user.setCreatedDate(java.time.LocalDateTime.now());
        user.setActive(true);
        
        // Deprecated: Direct repository save without proper error handling
        User savedUser = userRepository.save(user);
        
        // Deprecated: Manual event publishing
        eventPublisher.publishEvent(new UserCreatedEvent(savedUser));
        
        // Deprecated: Synchronous audit logging
        auditService.logUserCreation(savedUser);
        
        // Deprecated: Manual cache invalidation
        cacheManager.getCache("users").clear();
        cacheManager.getCache("userStats").clear();
        
        return savedUser;
    }

    /**
     * Deprecated: Legacy caching with complex key generation
     * Spring 6: Should use simpler caching patterns
     */
    @Cacheable(
        value = "users",
        key = "#id + '_' + #includeRoles + '_' + #includeDepartment",
        condition = "#id != null && #id > 0",
        unless = "#result == null"
    )
    public User findUserById(Long id, boolean includeRoles, boolean includeDepartment) {
        // Deprecated: Complex parameter-based data fetching
        User user = userRepository.findById(id).orElse(null);
        
        if (user != null) {
            // Deprecated: Manual lazy loading
            if (includeRoles) {
                user.getRoles().size(); // Force lazy loading
            }
            
            if (includeDepartment) {
                if (user.getDepartment() != null) {
                    user.getDepartment().getName(); // Force lazy loading
                }
            }
            
            // Deprecated: Manual audit logging in read operation
            auditService.logUserAccess(user.getId(), getCurrentUserId());
        }
        
        return user;
    }

    /**
     * Deprecated: Legacy async processing with Future
     * Spring 6: Should use CompletableFuture or reactive patterns
     */
    @Async("taskExecutor")  // Deprecated: Named executor reference
    public Future<List<User>> findUsersByDepartmentAsync(String departmentName) {
        try {
            // Deprecated: Thread.sleep simulation
            Thread.sleep(1000); // Simulate processing delay
            
            List<User> users = userRepository.findByDepartmentName(departmentName);
            
            // Deprecated: Manual result wrapping
            return new org.springframework.scheduling.annotation.AsyncResult<>(users);
            
        } catch (InterruptedException e) {
            // Deprecated: Exception handling in async method
            Thread.currentThread().interrupt();
            throw new RuntimeException("Async processing interrupted", e);
        }
    }

    /**
     * Deprecated: Legacy bulk operation with manual transaction handling
     * Spring 6: Should use batch processing patterns
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateUserStatusBulk(List<Long> userIds, boolean active) {
        int updatedCount = 0;
        
        // Deprecated: Manual iteration for bulk operations
        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null && user.getActive() != active) {
                    user.setActive(active);
                    userRepository.save(user);
                    updatedCount++;
                    
                    // Deprecated: Individual event publishing in bulk operation
                    eventPublisher.publishEvent(new UserStatusChangedEvent(user, active));
                }
                
                // Deprecated: Manual batch size control
                if (updatedCount % 100 == 0) {
                    // Force flush every 100 records
                    userRepository.flush();
                }
                
            } catch (Exception e) {
                // Deprecated: Individual error handling in bulk operation
                // Should use proper batch error handling in Spring 6
                continue;
            }
        }
        
        // Deprecated: Manual cache eviction after bulk operation
        evictUserCaches();
        
        return updatedCount;
    }

    /**
     * Deprecated: Legacy scheduled task with cron expression
     * Spring 6: Should use more flexible scheduling patterns
     */
    @Scheduled(cron = "0 0 2 * * ?")  // Daily at 2 AM
    public void cleanupInactiveUsers() {
        // Deprecated: Manual date calculation
        java.time.LocalDateTime cutoffDate = java.time.LocalDateTime.now().minusDays(365);
        
        // Deprecated: Find and delete pattern
        List<User> inactiveUsers = userRepository.findByActiveAndLastLoginDateBefore(false, cutoffDate);
        
        for (User user : inactiveUsers) {
            try {
                // Deprecated: Individual deletion in scheduled task
                userRepository.delete(user);
                
                // Deprecated: Synchronous email notification in scheduled task
                emailService.sendAccountDeletionNotification(user.getEmail());
                
                // Deprecated: Individual audit logging
                auditService.logUserDeletion(user.getId(), "SYSTEM_CLEANUP");
                
            } catch (Exception e) {
                // Deprecated: Error handling in scheduled task
                // Should use proper error handling and retry mechanisms
            }
        }
        
        // Deprecated: Manual statistics update
        updateUserStatistics();
    }

    /**
     * Deprecated: Legacy cache management with manual eviction
     * Spring 6: Should use declarative cache management
     */
    @CacheEvict(value = {"users", "userStats", "departmentUsers"}, allEntries = true)
    public void evictUserCaches() {
        // Deprecated: Manual cache clearing
        // Additional manual cache operations that should be declarative
    }

    /**
     * Deprecated: Legacy cache update with complex conditions
     * Spring 6: Should use simpler cache update patterns
     */
    @CachePut(
        value = "users",
        key = "#user.id",
        condition = "#user.active == true",
        unless = "#result == null"
    )
    public User updateUserProfile(User user) {
        // Deprecated: Manual validation and update logic
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            throw new UserNotFoundException("User not found: " + user.getId());
        }
        
        // Deprecated: Manual field copying
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        
        // Deprecated: Manual timestamp update
        existingUser.setLastModifiedDate(java.time.LocalDateTime.now());
        
        User updatedUser = userRepository.save(existingUser);
        
        // Deprecated: Manual related cache eviction
        cacheManager.getCache("departmentUsers").evict(updatedUser.getDepartment().getId());
        
        return updatedUser;
    }

    // Deprecated: Private helper methods with business logic
    private void loadDefaultConfiguration() {
        // Deprecated: Manual configuration loading
    }
    
    private void validateDependencies() {
        // Deprecated: Manual dependency validation
        if (userRepository == null) {
            throw new IllegalStateException("UserRepository not injected");
        }
    }
    
    private void initializeCache() {
        // Deprecated: Manual cache initialization
    }
    
    private void clearCaches() {
        // Deprecated: Manual cache clearing
    }
    
    private void closeConnections() {
        // Deprecated: Manual connection cleanup
    }
    
    private Long getCurrentUserId() {
        // Deprecated: Manual security context access
        return 1L; // Simplified for example
    }
    
    private void updateUserStatistics() {
        // Deprecated: Manual statistics calculation
    }
}

// Supporting classes and events
class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) { super(message); }
}

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
}

class UserCreatedEvent {
    private final User user;
    public UserCreatedEvent(User user) { this.user = user; }
    public User getUser() { return user; }
}

class UserStatusChangedEvent {
    private final User user;
    private final boolean newStatus;
    public UserStatusChangedEvent(User user, boolean newStatus) { 
        this.user = user; 
        this.newStatus = newStatus; 
    }
    public User getUser() { return user; }
    public boolean getNewStatus() { return newStatus; }
}

// Placeholder interfaces for dependencies
interface UserRepository extends org.springframework.data.jpa.repository.JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByDepartmentName(String departmentName);
    List<User> findByActiveAndLastLoginDateBefore(boolean active, java.time.LocalDateTime date);
    void flush();
}

interface DepartmentRepository extends org.springframework.data.jpa.repository.JpaRepository<Department, Long> {
}

interface EmailService {
    void sendAccountDeletionNotification(String email);
}

interface AuditService {
    void logUserCreation(User user);
    void logUserAccess(Long userId, Long accessorId);
    void logUserDeletion(Long userId, String reason);
}
