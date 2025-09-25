package com.redhat.mta.examples.springboot.quarkus.reactive;

import com.redhat.mta.examples.springboot.quarkus.dto.UserResponse;
import com.redhat.mta.examples.springboot.quarkus.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Reactive User Controller demonstrating Spring WebFlux patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @RestController with @Path
 * - Replace Mono<T> with Uni<T> (Mutiny)
 * - Replace Flux<T> with Multi<T> (Mutiny)
 * - Replace WebFlux operators with Mutiny operators
 * - Replace @RequestMapping with JAX-RS annotations
 * - Replace reactive ServerResponse with JAX-RS Response
 */
@RestController
@RequestMapping("/api/reactive/users")
public class ReactiveUserController {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveUserController.class);

    @Autowired
    private ReactiveUserService reactiveUserService;

    /**
     * Get all users reactively - Flux<T> → Multi<T>
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserResponse> streamAllUsers() {
        logger.debug("Streaming all users reactively");
        
        return reactiveUserService.findAllUsers()
                .map(this::convertToResponse)
                .delayElements(Duration.ofMillis(100)) // Simulate streaming delay
                .doOnNext(user -> logger.debug("Streaming user: {}", user.getUsername()))
                .onErrorResume(throwable -> {
                    logger.error("Error streaming users", throwable);
                    return Flux.empty();
                });
    }

    /**
     * Get user by ID reactively - Mono<T> → Uni<T>
     */
    @GetMapping("/{id}")
    public Mono<UserResponse> getUser(@PathVariable Long id) {
        logger.debug("Getting user reactively: {}", id);
        
        return reactiveUserService.findUserById(id)
                .map(this::convertToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + id)))
                .timeout(Duration.ofSeconds(5))
                .retry(2)
                .cache(Duration.ofMinutes(1))
                .doOnSuccess(user -> logger.debug("Found user: {}", user.getUsername()))
                .doOnError(error -> logger.error("Error finding user {}: {}", id, error.getMessage()));
    }

    /**
     * Create user reactively with validation
     */
    @PostMapping
    public Mono<UserResponse> createUser(@RequestBody Mono<User> userMono) {
        logger.info("Creating user reactively");
        
        return userMono
                .flatMap(this::validateUser)
                .flatMap(reactiveUserService::saveUser)
                .map(this::convertToResponse)
                .doOnSuccess(user -> logger.info("Created user reactively: {}", user.getUsername()))
                .onErrorMap(throwable -> new RuntimeException("Failed to create user: " + throwable.getMessage()));
    }

    /**
     * Update user reactively
     */
    @PutMapping("/{id}")
    public Mono<UserResponse> updateUser(@PathVariable Long id, @RequestBody Mono<User> userMono) {
        logger.info("Updating user reactively: {}", id);
        
        return Mono.zip(
                reactiveUserService.findUserById(id),
                userMono
        )
        .flatMap(tuple -> {
            User existingUser = tuple.getT1();
            User updateData = tuple.getT2();
            
            // Update fields
            existingUser.setFirstName(updateData.getFirstName());
            existingUser.setLastName(updateData.getLastName());
            existingUser.setEmail(updateData.getEmail());
            
            return reactiveUserService.saveUser(existingUser);
        })
        .map(this::convertToResponse)
        .doOnSuccess(user -> logger.info("Updated user reactively: {}", user.getUsername()));
    }

    /**
     * Delete user reactively
     */
    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user reactively: {}", id);
        
        return reactiveUserService.deleteUser(id)
                .doOnSuccess(aVoid -> logger.info("Deleted user reactively: {}", id));
    }

    /**
     * Search users with reactive pagination
     */
    @GetMapping("/search")
    public Flux<UserResponse> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Searching users reactively: name={}, page={}, size={}", name, page, size);
        
        return reactiveUserService.searchUsers(name, page, size)
                .map(this::convertToResponse)
                .take(size)
                .skip(page * size)
                .doOnComplete(() -> logger.debug("Completed user search"));
    }

    /**
     * Get user count reactively
     */
    @GetMapping("/count")
    public Mono<Long> getUserCount() {
        return reactiveUserService.countUsers()
                .doOnSuccess(count -> logger.debug("User count: {}", count));
    }

    /**
     * Process users in batch reactively
     */
    @PostMapping("/batch-process")
    public Flux<UserResponse> batchProcessUsers(@RequestBody Flux<User> userFlux) {
        logger.info("Processing users in batch reactively");
        
        return userFlux
                .buffer(10) // Process in batches of 10
                .flatMap(userBatch -> 
                    Flux.fromIterable(userBatch)
                        .flatMap(this::processUser)
                        .collectList()
                        .flatMapMany(Flux::fromIterable)
                )
                .map(this::convertToResponse)
                .doOnComplete(() -> logger.info("Completed batch processing"));
    }

    /**
     * Server-Sent Events endpoint for real-time updates
     */
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> userEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "User event " + sequence + " at " + LocalDateTime.now())
                .take(100) // Limit to 100 events
                .doOnNext(event -> logger.debug("Sending event: {}", event));
    }

    // Helper methods
    
    private Mono<User> validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Username is required"));
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            return Mono.error(new IllegalArgumentException("Valid email is required"));
        }
        return Mono.just(user);
    }

    private Mono<User> processUser(User user) {
        // Simulate some processing
        return Mono.just(user)
                .delayElement(Duration.ofMillis(50))
                .map(u -> {
                    u.setUpdatedAt(LocalDateTime.now());
                    return u;
                });
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setBio(user.getBio());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getRoles() != null) {
            response.setRoles(user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet()));
        }
        
        return response;
    }
}
