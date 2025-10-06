package com.redhat.mta.examples.springboot.quarkus.reactive;

import com.redhat.mta.examples.springboot.quarkus.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive User Repository demonstrating Spring Data R2DBC patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace R2dbcRepository with Hibernate Reactive Panache
 * - Replace @Repository with @ApplicationScoped
 * - Replace Mono<T>/Flux<T> with Uni<T>/Multi<T> (Mutiny)
 * - Replace @Query with Panache query methods
 * - Replace reactive database operations with Hibernate Reactive
 */
@Repository
public interface ReactiveUserRepository extends R2dbcRepository<User, Long> {

    /**
     * Find users by active status - Flux<T> → Multi<T>
     */
    Flux<User> findByActive(Boolean active);

    /**
     * Find user by username - Mono<T> → Uni<T>
     */
    Mono<User> findByUsername(String username);

    /**
     * Find user by email - Mono<T> → Uni<T>
     */
    Mono<User> findByEmail(String email);

    /**
     * Find users by name containing (case insensitive) - Flux<T> → Multi<T>
     */
    Flux<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    /**
     * Find users created after date - Flux<T> → Multi<T>
     */
    Flux<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users by email domain using custom query
     */
    @Query("SELECT * FROM users WHERE email LIKE CONCAT('%@', :domain)")
    Flux<User> findByEmailDomain(@Param("domain") String domain);

    /**
     * Count active users - Mono<Long> → Uni<Long>
     */
    Mono<Long> countByActive(Boolean active);

    /**
     * Find top users by creation date - Flux<T> → Multi<T>
     */
    Flux<User> findTop10ByActiveOrderByCreatedAtDesc(Boolean active);

    /**
     * Custom query to find users with complex conditions
     */
    @Query("SELECT * FROM users WHERE active = true AND created_at >= :since AND (first_name ILIKE :name OR last_name ILIKE :name)")
    Flux<User> findActiveUsersWithNameSince(@Param("name") String name, @Param("since") LocalDateTime since);

    /**
     * Delete users by active status - Mono<Integer> → Uni<Integer>
     */
    Mono<Integer> deleteByActive(Boolean active);

    /**
     * Update user last login time
     */
    @Query("UPDATE users SET updated_at = :timestamp WHERE id = :id")
    Mono<Integer> updateLastLogin(@Param("id") Long id, @Param("timestamp") LocalDateTime timestamp);

    /**
     * Find users with pagination simulation
     */
    @Query("SELECT * FROM users WHERE active = true ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    Flux<User> findActiveUsersWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    /**
     * Aggregate query - count users by domain
     */
    @Query("SELECT SUBSTRING(email FROM POSITION('@' IN email) + 1) as domain, COUNT(*) as count " +
           "FROM users WHERE email IS NOT NULL GROUP BY domain")
    Flux<DomainCount> countUsersByEmailDomain();

    /**
     * Find users by multiple IDs - Flux<T> → Multi<T>
     */
    @Query("SELECT * FROM users WHERE id IN (:ids)")
    Flux<User> findByIdIn(@Param("ids") Iterable<Long> ids);

    /**
     * Check if user exists by email - Mono<Boolean> → Uni<Boolean>
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Find users with roles (simulated join)
     */
    @Query("SELECT u.* FROM users u JOIN user_roles ur ON u.id = ur.user_id WHERE ur.role_id = :roleId")
    Flux<User> findUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * Inner class for domain count result
     */
    interface DomainCount {
        String getDomain();
        Long getCount();
    }
}


