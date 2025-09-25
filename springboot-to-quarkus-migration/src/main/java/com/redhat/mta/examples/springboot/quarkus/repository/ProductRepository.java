package com.redhat.mta.examples.springboot.quarkus.repository;

import com.redhat.mta.examples.springboot.quarkus.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product Repository demonstrating Spring Data JPA patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @Repository with @ApplicationScoped
 * - Replace JpaRepository with PanacheRepository<Product>
 * - Replace query methods with Panache equivalents
 * - Replace @Modifying queries with Panache update/delete methods
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Basic finder methods
    List<Product> findByAvailable(Boolean available);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    Optional<Product> findByName(String name);

    // Price-based queries
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    List<Product> findByPriceLessThan(BigDecimal price);

    // Stock-based queries
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    
    List<Product> findByStockQuantityLessThanEqual(Integer quantity);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    // Category and availability combinations
    List<Product> findByCategoryAndAvailable(String category, Boolean available);
    
    List<Product> findByCategoryAndPriceLessThan(String category, BigDecimal maxPrice);

    // Pagination queries
    Page<Product> findByAvailable(Boolean available, Pageable pageable);
    
    Page<Product> findByCategory(String category, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Custom queries with JPQL
    @Query("SELECT p FROM Product p WHERE p.available = true AND p.stockQuantity > 0 ORDER BY p.name")
    List<Product> findAvailableProductsInStock();

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByCategoryAndPriceRange(@Param("category") String category,
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.available = true ORDER BY p.category")
    List<String> findAvailableCategories();

    // Native queries
    @Query(value = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(description) LIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT AVG(price) FROM products WHERE category = :category", nativeQuery = true)
    BigDecimal findAveragePriceByCategory(@Param("category") String category);

    // Modifying queries
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.available = :available WHERE p.id = :id")
    int updateProductAvailability(@Param("id") Long id, @Param("available") Boolean available);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :id")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.available = false WHERE p.stockQuantity = 0")
    int markOutOfStockProductsUnavailable();

    // Count queries
    long countByCategory(String category);
    
    long countByAvailable(Boolean available);
    
    long countByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Exists queries
    boolean existsByName(String name);
    
    boolean existsByCategory(String category);

    // Top/First queries
    List<Product> findTop10ByAvailableOrderByPriceAsc(Boolean available);
    
    List<Product> findFirst5ByCategoryOrderByNameAsc(String category);

    // Complex aggregation queries
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.available = true GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> findProductCountByCategory();

    @Query("SELECT p.category, MIN(p.price), MAX(p.price), AVG(p.price) FROM Product p GROUP BY p.category")
    List<Object[]> findPriceStatisticsByCategory();

    // Subquery examples
    @Query("SELECT p FROM Product p WHERE p.price > (SELECT AVG(p2.price) FROM Product p2 WHERE p2.category = p.category)")
    List<Product> findProductsAboveAveragePriceInCategory();

    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT DISTINCT oi.product.id FROM OrderItem oi)")
    List<Product> findProductsWithOrders();
}
