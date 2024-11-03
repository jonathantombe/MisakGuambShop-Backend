package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.ProductDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.Product;
import com.misakguambshop.app.model.ProductImage;
import com.misakguambshop.app.repository.ProductRepository;
import com.misakguambshop.app.security.JwtAuthenticationFilter;
import com.misakguambshop.app.security.UserPrincipal;
import com.misakguambshop.app.service.CloudinaryService;
import com.misakguambshop.app.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);



    @Autowired
    public ProductController(ProductService productService, CloudinaryService cloudinaryService) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'USER')")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'USER')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'USER')")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasAnyAuthority('SELLER', 'USER')")
    public ResponseEntity<List<ProductDto>> getMyProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        List<Product> userProducts = productService.getProductsByUserId(userId);
        List<ProductDto> userProductDTOs = userProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userProductDTOs);
    }

    @GetMapping("/my-approved")
    @PreAuthorize("hasAnyAuthority('SELLER', 'USER')")
    public ResponseEntity<List<ProductDto>> getMyApprovedProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();

        List<Product> approvedProducts = productService.getApprovedProductsByUserId(userId);
        List<ProductDto> approvedProductDTOs = approvedProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(approvedProductDTOs);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ProductDto>> getPendingProducts() {
        logger.info("Fetching pending products");
        List<Product> pendingProducts = productService.getPendingProducts();
        List<ProductDto> pendingProductDTOs = pendingProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Found {} pending products", pendingProductDTOs.size());
        return ResponseEntity.ok(pendingProductDTOs);
    }

    private ProductDto convertToDTO(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setUserId(product.getUser() != null ? product.getUser().getId() : null);
        dto.setStock(product.getStock());
        dto.setStatus(product.getStatus());
        dto.setRejectionReason(product.getRejectionReason());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        dto.setImageUrls(imageUrls);
        return dto;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam Integer stock,
            @RequestParam Long categoryId,
            @RequestParam(value = "image", required = false) List<MultipartFile> images) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Long userId = userPrincipal.getId();

            ProductDto productDto = new ProductDto();
            productDto.setName(name);
            productDto.setDescription(description);
            productDto.setPrice(price);
            productDto.setStock(stock);
            productDto.setCategoryId(categoryId);
            productDto.setUserId(userId);

            Product createdProduct = productService.createProduct(productDto, images);

            // Aquí está el cambio principal - Convertimos el producto a DTO para incluir toda la información
            ProductDto createdProductDto = convertToDTO(createdProduct);
            Map<String, Object> response = new HashMap<>();
            response.put("product", createdProductDto);  // Incluimos el producto completo
            response.put("message", "Su producto está en proceso de revisión y se ha guardado como 'pendiente'. Recibirá una notificación una vez que se apruebe. ¡Gracias por contribuir a nuestra comunidad!");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating product", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error creating product");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/approve/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> approveProduct(@PathVariable Long productId) {
        try {
            Product approvedProduct = productService.approveProduct(productId);
            ProductDto approvedProductDto = convertToDTO(approvedProduct);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto aprobado con éxito");
            response.put("product", approvedProductDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al aprobar el producto: " + e.getMessage());
        }
    }

    @PostMapping("/reject/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> rejectProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, String> payload) {
        try {
            String reason = payload.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reason for rejection is required");
            }
            Product rejectedProduct = productService.rejectProduct(productId, reason);
            ProductDto rejectedProductDto = convertToDTO(rejectedProduct);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto rechazado con éxito");
            response.put("product", rejectedProductDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al rechazar el producto: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductDto productDto,
            @RequestParam(value = "image", required = false) List<MultipartFile> image) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto, image));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
    public ResponseEntity<Product> patchProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId) {

        ProductDto productDto = new ProductDto();
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setPrice(price);
        productDto.setStock(stock);
        productDto.setCategoryId(categoryId);
        productDto.setUserId(userId);

        return ResponseEntity.ok(productService.patchProduct(id, productDto, null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}