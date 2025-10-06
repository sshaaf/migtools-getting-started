package com.redhat.mta.examples.springboot.quarkus.repository;

import com.redhat.mta.examples.springboot.quarkus.model.Order;
import com.redhat.mta.examples.springboot.quarkus.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository demonstrating Spring Data JPA patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Repository with @ApplicationScoped
 * - Replace JpaRepository with PanacheRepository<Order>
 * - Replace query methods with Panache equivalents
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Basic finder methods
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUser(User user);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(Order.OrderStatus status);

    // Date-based queries
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findByCreatedAtAfter(LocalDateTime date);
    
    List<Order> findByCreatedAtBefore(LocalDateTime date);

    // User and status combinations
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
    
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);

    // Amount-based queries
    List<Order> findByTotalAmountGreaterThan(BigDecimal amount);
    
    List<Order> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // Pagination queries
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Custom JPQL queries
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findUserOrdersOrderedByDate(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.status = :status")
    List<Order> findByStatusWithUser(@Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user.email = :email AND o.status = :status")
    List<Order> findByUserEmailAndStatus(@Param("email") String email, @Param("status") Order.OrderStatus status);

    // Aggregation queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countOrdersByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId AND o.status = 'DELIVERED'")
    BigDecimal getTotalSpentByUser(@Param("userId") Long userId);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderCountByStatus();

    @Query("SELECT DATE(o.createdAt), COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.createdAt >= :fromDate GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> getDailySalesReport(@Param("fromDate") LocalDateTime fromDate);

    // Native queries
    @Query(value = "SELECT * FROM orders WHERE user_id = :userId AND total_amount > :minAmount ORDER BY created_at DESC", nativeQuery = true)
    List<Order> findUserOrdersAboveAmount(@Param("userId") Long userId, @Param("minAmount") BigDecimal minAmount);

    @Query(value = "SELECT AVG(total_amount) FROM orders WHERE status = :status", nativeQuery = true)
    BigDecimal getAverageOrderAmountByStatus(@Param("status") String status);

    // Complex queries with subqueries
    @Query("SELECT o FROM Order o WHERE o.totalAmount > (SELECT AVG(o2.totalAmount) FROM Order o2)")
    List<Order> findOrdersAboveAverageAmount();

    @Query("SELECT o FROM Order o WHERE o.user.id IN (SELECT DISTINCT o2.user.id FROM Order o2 WHERE o2.status = 'DELIVERED' GROUP BY o2.user.id HAVING COUNT(o2) >= :minOrders)")
    List<Order> findOrdersFromFrequentCustomers(@Param("minOrders") Long minOrders);

    // Count and exists queries
    long countByStatus(Order.OrderStatus status);
    
    long countByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsByOrderNumber(String orderNumber);
    
    boolean existsByUserIdAndStatus(Long userId, Order.OrderStatus status);

    // Top/First queries
    List<Order> findTop10ByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    
    List<Order> findFirst5ByUserOrderByCreatedAtDesc(User user);

    // Recent orders
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :date ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);

    // Orders by multiple statuses
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findByStatusIn(@Param("statuses") List<Order.OrderStatus> statuses);

    // Monthly/yearly reports
    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) ORDER BY YEAR(o.createdAt) DESC, MONTH(o.createdAt) DESC")
    List<Object[]> getMonthlySalesReport();
}

