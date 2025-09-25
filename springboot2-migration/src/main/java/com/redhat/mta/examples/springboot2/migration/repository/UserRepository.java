package com.redhat.mta.examples.springboot2.migration.repository;

import com.redhat.mta.examples.springboot2.migration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User repository using Spring Data JPA patterns.
 * 
 * Note: While not deprecated, this shows common Spring Boot 2 patterns
 * that might need adjustment in Spring Boot 3 migrations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    
    long countByActiveTrue();
    
    List<User> findByActiveTrue();
    
    List<User> findByActiveFalse();
    
    Optional<User> findByEmail(String email);
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.active = :active")
    List<User> findByNameAndActiveStatus(@Param("name") String name, @Param("active") boolean active);
    
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findUserByEmailNative(String email);
}
