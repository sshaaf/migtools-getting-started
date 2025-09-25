package com.redhat.mta.examples.spring.framework.repository;

import com.redhat.mta.examples.spring.framework.model.User;
import com.redhat.mta.examples.spring.framework.model.Department;
import com.redhat.mta.examples.spring.framework.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository using deprecated patterns.
 * 
 * Deprecated patterns for Spring Framework 6 migration:
 * - Legacy @Query syntax patterns
 * - Deprecated parameter binding approaches
 * - Legacy pagination and sorting patterns
 * - Deprecated repository method naming conventions
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Deprecated: Legacy @Query with positional parameters
     * Spring 6: Should use named parameters for better maintainability
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.active = ?2")
    Optional<User> findByEmailAndActiveStatus(String email, Boolean active);

    /**
     * Deprecated: Legacy native query with positional parameters
     * Spring 6: Should use named parameters and avoid native queries when possible
     */
    @Query(value = "SELECT * FROM users WHERE department = ?1 AND salary > ?2", nativeQuery = true)
    List<User> findByDepartmentAndSalaryGreaterThan(String department, Double salary);

    /**
     * Deprecated: Legacy @Query with mixed parameter binding
     * Mixing positional and named parameters is deprecated
     */
    @Query("SELECT u FROM User u WHERE u.firstName = ?1 AND u.lastName = :lastName")
    List<User> findByFirstNameAndLastName(String firstName, @Param("lastName") String lastName);

    /**
     * Deprecated: Legacy pagination query without proper sorting
     * Spring 6: Should use proper Sort and Pageable patterns
     */
    @Query("SELECT u FROM User u WHERE u.createdDate >= ?1")
    Page<User> findUsersCreatedAfter(java.time.LocalDateTime date, Pageable pageable);

    /**
     * Deprecated: Legacy method naming convention with complex criteria
     * Spring 6: Should use @Query or Criteria API for complex queries
     */
    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndActiveTrue(
        String firstName, String lastName);

    /**
     * Deprecated: Legacy @Query with JOIN FETCH patterns
     * Spring 6: Should use EntityGraph or proper fetch strategies
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.department WHERE u.id = ?1")
    Optional<User> findByIdWithRolesAndDepartment(Long id);

    /**
     * Deprecated: Legacy bulk update query
     * Spring 6: Should use proper transaction management and bulk operations
     */
    @Query("UPDATE User u SET u.lastLoginDate = ?2 WHERE u.id = ?1")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int updateLastLoginDate(Long userId, java.time.LocalDateTime loginDate);

    /**
     * Deprecated: Legacy delete query with positional parameters
     * Spring 6: Should use proper delete methods or named parameters
     */
    @Query("DELETE FROM User u WHERE u.email = ?1 AND u.active = false")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    int deleteInactiveUserByEmail(String email);

    /**
     * Deprecated: Legacy repository method with complex return type
     * Spring 6: Should use proper projection or DTO patterns
     */
    @Query("SELECT u.id, u.email, u.firstName, u.lastName, d.name FROM User u JOIN u.department d WHERE u.active = true")
    List<Object[]> findActiveUsersWithDepartmentInfo();

    /**
     * Deprecated: Legacy @Query with hardcoded values
     * Spring 6: Should use parameterized queries for all dynamic values
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.active = true ORDER BY u.createdDate DESC")
    List<User> findActiveAdminUsers();

    /**
     * Deprecated: Legacy method with excessive parameters
     * Spring 6: Should use Specification API or custom criteria
     */
    List<User> findByFirstNameAndLastNameAndEmailAndDepartmentNameAndActiveAndCreatedDateBetween(
        String firstName, String lastName, String email, String departmentName, 
        Boolean active, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    /**
     * Deprecated: Legacy @Query with subquery patterns
     * Spring 6: Should use proper JOIN or EXISTS clauses
     */
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT ur.userId FROM UserRole ur WHERE ur.roleId = ?1)")
    List<User> findUsersByRoleId(Long roleId);

    /**
     * Deprecated: Legacy count query with complex conditions
     * Spring 6: Should use derived count methods or Criteria API
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.department.name = ?1 AND u.active = true AND u.salary BETWEEN ?2 AND ?3")
    Long countActiveUsersByDepartmentAndSalaryRange(String departmentName, Double minSalary, Double maxSalary);

    /**
     * Deprecated: Legacy custom repository method naming
     * Spring 6: Should follow consistent naming conventions
     */
    List<User> findByEmailIgnoreCase(String email);
    List<User> findByFirstNameStartingWithIgnoreCase(String prefix);
    List<User> findByLastNameEndingWithIgnoreCase(String suffix);
    List<User> findByActiveOrderByCreatedDateDesc(Boolean active);
    
    /**
     * Deprecated: Legacy @Query with ORDER BY and LIMIT simulation
     * Spring 6: Should use Pageable for pagination and sorting
     */
    @Query("SELECT u FROM User u WHERE u.department.name = ?1 ORDER BY u.salary DESC")
    List<User> findTopUsersByDepartmentOrderBySalary(String departmentName);
}
