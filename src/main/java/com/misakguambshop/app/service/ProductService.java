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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, SellerRepository sellerRepository, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sellerRepository = sellerRepository;
        this.cloudinaryService = cloudinaryService;
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

    @Transactional
    public Product createProduct(ProductDto productDto, MultipartFile image) {
        if (productRepository.existsByNameAndSellerId(productDto.getName(), productDto.getSellerId())) {
            throw new IllegalArgumentException("A product with this name already exists for this seller");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
        product.setCategory(category);

        Seller seller = sellerRepository.findById(productDto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + productDto.getSellerId()));
        product.setSeller(seller);

        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image);
            product.setImageUrl(imageUrl);
        }

        product.setCreationDate(LocalDateTime.now());
        product.setUpdateDate(LocalDateTime.now());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductDto productDto, MultipartFile image) {
        Product product = getProductById(id);

        if (!product.getName().equals(productDto.getName()) &&
                productRepository.existsByNameAndSellerId(productDto.getName(), productDto.getSellerId())) {
            throw new IllegalArgumentException("A product with this name already exists for this seller");
        }

        updateProductFromDto(product, productDto);

        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image);
            product.setImageUrl(imageUrl);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product patchProduct(Long id, ProductDto productDto, MultipartFile image) {
        Product product = getProductById(id);

        if (productDto.getName() != null && !productDto.getName().isEmpty()) {
            if (!product.getName().equals(productDto.getName()) &&
                    productRepository.existsByNameAndSellerId(productDto.getName(), product.getSeller().getId())) {
                throw new IllegalArgumentException("A product with this name already exists for this seller");
            }
            product.setName(productDto.getName());
        }

        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }

        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }

        if (productDto.getStock() != null) {
            product.setStock(productDto.getStock());
        }

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }

        if (productDto.getSellerId() != null) {
            Seller seller = sellerRepository.findById(productDto.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + productDto.getSellerId()));
            product.setSeller(seller);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image);
            product.setImageUrl(imageUrl);
        }

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
        product.setStock(productDto.getStock());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }

        if (productDto.getSellerId() != null) {
            Seller seller = sellerRepository.findById(productDto.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + productDto.getSellerId()));
            product.setSeller(seller);
        }
    }
}
