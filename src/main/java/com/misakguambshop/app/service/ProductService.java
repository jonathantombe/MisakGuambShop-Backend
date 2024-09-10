package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.ProductDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Category;
import com.misakguambshop.app.model.Product;
import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.repository.CategoryRepository;
import com.misakguambshop.app.repository.ProductRepository;
import com.misakguambshop.app.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sellerRepository = sellerRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public Product createProduct(ProductDto productDto) {
        if (productRepository.existsByNameAndSellerId(productDto.getName(), productDto.getSellerId())) {
            throw new IllegalArgumentException("A product with this name already exists for this seller");
        }

        Product product = new Product();
        updateProductFromDto(product, productDto);

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductDto productDto) {
        Product product = getProductById(id);

        if (!product.getName().equals(productDto.getName()) &&
                productRepository.existsByNameAndSellerId(productDto.getName(), productDto.getSellerId())) {
            throw new IllegalArgumentException("A product with this name already exists for this seller");
        }

        updateProductFromDto(product, productDto);

        return productRepository.save(product);
    }

    public Product patchProduct(Long id, Map<String, Object> updates) {
        Product product = getProductById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    if (value != null && !product.getName().equals(value) &&
                            productRepository.existsByNameAndSellerId((String) value, product.getSeller().getId())) {
                        throw new IllegalArgumentException("A product with this name already exists for this seller");
                    }
                    product.setName((String) value);
                    break;
                case "description":
                    product.setDescription((String) value);
                    break;
                case "price":
                    product.setPrice(new BigDecimal(value.toString()));
                    break;
                case "categoryId":
                    if (value != null) {
                        Category category = categoryRepository.findById(Long.valueOf(value.toString()))
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + value));
                        product.setCategory(category);
                    } else {
                        product.setCategory(null);
                    }
                    break;
                case "imageUrl":
                    product.setImageUrl((String) value);
                    break;
                case "stock":
                    product.setStock((Integer) value);
                    break;
            }
        });

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    private void updateProductFromDto(Product product, ProductDto productDto) {
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setStock(productDto.getStock());

        Category category = productDto.getCategoryId() != null ?
                categoryRepository.findById(productDto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()))
                : null;
        product.setCategory(category);

        Seller seller = sellerRepository.findById(productDto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + productDto.getSellerId()));
        product.setSeller(seller);
    }
}
