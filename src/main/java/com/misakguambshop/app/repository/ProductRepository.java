package com.misakguambshop.app.repository;

import com.misakguambshop.app.model.Product;
import com.misakguambshop.app.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByStatusIn(List<ProductStatus> statuses);

    boolean existsByNameAndUserId(String name, Long userId);

    List<Product> findByUserIdAndStatus(Long userId, ProductStatus status);

    List<Product> findByUserId(Long userId);
    List<Product> findByStatus(String status);

    // Nuevos métodos para cargar productos con imágenes
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.category.id = :categoryId")
    List<Product> findByCategoryIdWithImages(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.status = :status")
    List<Product> findByStatusWithImages(@Param("status") ProductStatus status);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.user.id = :userId AND p.status = :status")
    List<Product> findByUserIdAndStatusWithImages(@Param("userId") Long userId, @Param("status") ProductStatus status);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.user.id = :userId")
    List<Product> findByUserIdWithImages(@Param("userId") Long userId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.images WHERE p.user.id = :userId")
    List<Product> findByUserIdWithCategoryAndImages(@Param("userId") Long userId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndImages(@Param("id") Long id);


    @Query("SELECT p FROM Product p WHERE p.stock > :minStock AND p.status = :status")
    List<Product> findByStockGreaterThanAndStatus(@Param("minStock") Integer minStock, @Param("status") ProductStatus status);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "p.category.name LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "p.status = :status")
    List<Product> searchApprovedProducts(
            @Param("query") String query,
            @Param("status") ProductStatus status
    );
}
