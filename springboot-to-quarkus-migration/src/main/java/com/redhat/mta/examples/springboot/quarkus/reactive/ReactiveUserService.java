package com.redhat.mta.examples.springboot.quarkus.reactive;

import com.redhat.mta.examples.springboot.quarkus.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Reactive User Service demonstrating Spring WebFlux patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Service with @ApplicationScoped
 * - Replace Mono<T> with Uni<T> (Mutiny)
 * - Replace Flux<T> with Multi<T> (Mutiny)
 * - Replace Reactor operators with Mutiny operators
 * - Replace reactive repository with Hibernate Reactive Panache
 */
@Service
public class ReactiveUserService {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveUserService.class);

    @Autowired
    private ReactiveUserRepository reactiveUserRepository;

    /**
     * Find all users reactively - Flux<T> → Multi<T>
     */
    public Flux<User> findAllUsers() {
        logger.debug("Finding all users reactively");
        
        return reactiveUserRepository.findAll()
                .filter(user -> user.getActive())
                .sort((u1, u2) -> u1.getCreatedAt().compareTo(u2.getCreatedAt()))
                .doOnNext(user -> logger.debug("Processing user: {}", user.getUsername()))
                .onErrorResume(throwable -> {
                    logger.error("Error finding all users", throwable);
                    return Flux.empty();
                });
    }

    /**
     * Find user by ID reactively - Mono<T> → Uni<T>
     */
    public Mono<User> findUserById(Long id) {
        logger.debug("Finding user by ID reactively: {}", id);
        
        return reactiveUserRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + id)))
                .timeout(Duration.ofSeconds(5))
                .cache(Duration.ofMinutes(2))
                .doOnSuccess(user -> logger.debug("Found user: {}", user.getUsername()));
    }

    /**
     * Save user reactively with validation
     */
    public Mono<User> saveUser(User user) {
        logger.info("Saving user reactively: {}", user.getUsername());
        
        return Mono.fromCallable(() -> {
            // Simulate validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            return user;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(validUser -> {
            validUser.setUpdatedAt(LocalDateTime.now());
            if (validUser.getCreatedAt() == null) {
                validUser.setCreatedAt(LocalDateTime.now());
            }
            return reactiveUserRepository.save(validUser);
        })
        .doOnSuccess(savedUser -> logger.info("Saved user reactively: {}", savedUser.getUsername()))
        .onErrorMap(throwable -> new RuntimeException("Failed to save user: " + throwable.getMessage()));
    }

    /**
     * Delete user reactively
     */
    public Mono<Void> deleteUser(Long id) {
        logger.info("Deleting user reactively: {}", id);
        
        return reactiveUserRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + id)))
                .flatMap(user -> reactiveUserRepository.delete(user))
                .doOnSuccess(aVoid -> logger.info("Deleted user reactively: {}", id));
    }

    /**
     * Search users by name reactively
     */
    public Flux<User> searchUsers(String name, int page, int size) {
        logger.debug("Searching users reactively: name={}", name);
        
        Flux<User> userFlux = name != null && !name.trim().isEmpty() ?
                reactiveUserRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name) :
                reactiveUserRepository.findAll();
                
        return userFlux
                .filter(User::getActive)
                .skip(page * size)
                .take(size)
                .doOnNext(user -> logger.debug("Found user in search: {}", user.getUsername()));
    }

    /**
     * Count users reactively
     */
    public Mono<Long> countUsers() {
        logger.debug("Counting users reactively");
        
        return reactiveUserRepository.count()
                .doOnSuccess(count -> logger.debug("Total users: {}", count));
    }

    /**
     * Find users by email domain reactively
     */
    public Flux<User> findUsersByEmailDomain(String domain) {
        logger.debug("Finding users by email domain: {}", domain);
        
        return reactiveUserRepository.findAll()
                .filter(user -> user.getEmail() != null && user.getEmail().endsWith("@" + domain))
                .sort((u1, u2) -> u1.getEmail().compareTo(u2.getEmail()))
                .doOnNext(user -> logger.debug("Found user with domain {}: {}", domain, user.getEmail()));
    }

    /**
     * Batch update users reactively
     */
    public Flux<User> batchUpdateUsers(Flux<User> userFlux) {
        logger.info("Batch updating users reactively");
        
        return userFlux
                .buffer(10) // Process in batches of 10
                .flatMap(userBatch -> 
                    Flux.fromIterable(userBatch)
                        .flatMap(user -> {
                            user.setUpdatedAt(LocalDateTime.now());
                            return reactiveUserRepository.save(user);
                        })
                        .collectList()
                        .flatMapMany(Flux::fromIterable)
                )
                .doOnNext(user -> logger.debug("Updated user in batch: {}", user.getUsername()))
                .doOnComplete(() -> logger.info("Completed batch update"));
    }

    /**
     * Find active users with complex reactive operations
     */
    public Flux<User> findActiveUsersWithProcessing() {
        return reactiveUserRepository.findByActive(true)
                .filter(user -> user.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .flatMap(this::enrichUserData)
                .groupBy(user -> user.getEmail().substring(user.getEmail().indexOf("@") + 1))
                .flatMap(groupedFlux -> 
                    groupedFlux.collectList()
                        .flatMapMany(Flux::fromIterable)
                )
                .sort((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .take(100);
    }

    /**
     * Async processing with CompletableFuture integration
     */
    public Mono<User> processUserAsync(Long userId) {
        logger.info("Processing user asynchronously: {}", userId);
        
        return Mono.fromFuture(
                CompletableFuture.supplyAsync(() -> {
                    // Simulate heavy processing
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return userId;
                })
        )
        .flatMap(this::findUserById)
        .map(user -> {
            user.setBio("Processed asynchronously at " + LocalDateTime.now());
            return user;
        })
        .flatMap(this::saveUser)
        .doOnSuccess(user -> logger.info("Completed async processing for user: {}", user.getUsername()));
    }

    // Helper methods
    
    private Mono<User> enrichUserData(User user) {
        // Simulate enriching user data from external service
        return Mono.just(user)
                .delayElement(Duration.ofMillis(10))
                .map(u -> {
                    if (u.getBio() == null) {
                        u.setBio("Enriched user data");
                    }
                    return u;
                });
    }
}


