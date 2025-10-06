package com.redhat.mta.examples.springboot.quarkus.repository;

import com.redhat.mta.examples.springboot.quarkus.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Role Repository demonstrating Spring Data JPA patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Repository with @ApplicationScoped
 * - Replace JpaRepository with PanacheRepository<Role>
 * - Replace query methods with Panache equivalents
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Basic finder methods
    Optional<Role> findByName(String name);
    
    List<Role> findByActive(Boolean active);
    
    List<Role> findByNameContainingIgnoreCase(String name);

    // Custom queries
    @Query("SELECT r FROM Role r WHERE r.active = true ORDER BY r.name")
    List<Role> findAllActiveRoles();

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    // Count and exists queries
    boolean existsByName(String name);
    
    long countByActive(Boolean active);
}

