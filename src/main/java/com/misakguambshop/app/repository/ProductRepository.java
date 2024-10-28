package com.misakguambshop.app.repository;

import com.misakguambshop.app.model.Product;
import com.misakguambshop.app.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByStatus(ProductStatus status);
    boolean existsByNameAndUserId(String name, Long userId);
    List<Product> findByUserIdAndStatus(Long userId, ProductStatus status);
    List<Product> findByUserId(Long userId);
}
