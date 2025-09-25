package com.redhat.mta.examples.springboot.quarkus.repository;

import com.redhat.mta.examples.springboot.quarkus.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository demonstrating Spring Data JPA patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Repository with @ApplicationScoped
 * - Replace JpaRepository with PanacheRepository<User>
 * - Replace @Query with Panache query methods
 * - Replace @Modifying with Panache update/delete methods
 * - Replace Pageable/Page with Panache paging
 * - Replace Sort with Panache Sort
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic finder methods - will be replaced with Panache equivalents
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByActive(Boolean active);
    
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    // Complex query methods - will be replaced with Panache queries
    @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    List<User> findByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.createdAt >= :date")
    List<User> findActiveUsersCreatedAfter(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.favoriteProducts WHERE u.id = :id")
    Optional<User> findByIdWithFavoriteProducts(@Param("id") Long id);

    // Native queries - will be replaced with Panache queries
    @Query(value = "SELECT * FROM users WHERE email LIKE %:domain%", nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);

    @Query(value = "SELECT COUNT(*) FROM users WHERE active = true", nativeQuery = true)
    Long countActiveUsers();

    // Pagination and sorting - will be replaced with Panache paging
    Page<User> findByActiveOrderByCreatedAtDesc(Boolean active, Pageable pageable);
    
    List<User> findByActiveOrderByLastNameAsc(Boolean active);
    
    Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    // Modifying queries - will be replaced with Panache update/delete methods
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    int updateUserActiveStatus(@Param("id") Long id, @Param("active") Boolean active);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :ids")
    int updateLastModifiedForUsers(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.active = false AND u.createdAt < :date")
    int deleteInactiveUsersOlderThan(@Param("date") LocalDateTime date);

    // Complex queries with joins
    @Query("SELECT u FROM User u JOIN u.orders o WHERE o.status = 'COMPLETED' GROUP BY u HAVING COUNT(o) >= :minOrders")
    List<User> findUsersWithMinimumOrders(@Param("minOrders") Long minOrders);

    @Query("SELECT u FROM User u WHERE u.id IN (SELECT DISTINCT o.user.id FROM Order o WHERE o.createdAt >= :date)")
    List<User> findUsersWithRecentOrders(@Param("date") LocalDateTime date);

    // Exists queries
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);

    // Count queries
    long countByActive(Boolean active);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Custom method names that will need conversion
    List<User> findTop10ByActiveOrderByCreatedAtDesc(Boolean active);
    
    List<User> findFirst5ByOrderByUsernameAsc();

    // Methods using Sort parameter
    List<User> findByActive(Boolean active, Sort sort);

    // Advanced query with multiple conditions
    @Query("SELECT u FROM User u WHERE " +
           "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:active IS NULL OR u.active = :active)")
    Page<User> findUsersWithFilters(@Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("email") String email,
                                   @Param("active") Boolean active,
                                   Pageable pageable);
}
