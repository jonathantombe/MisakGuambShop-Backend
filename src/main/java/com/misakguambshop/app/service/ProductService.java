package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.ProductDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.*;
import com.misakguambshop.app.repository.*;
import com.misakguambshop.app.security.JwtAuthenticationFilter;
import com.misakguambshop.app.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository;

    @Autowired
    private JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, SellerRepository sellerRepository, CloudinaryService cloudinaryService, UserRepository userRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.sellerRepository = sellerRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
        this.productImageRepository = productImageRepository;
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


    public List<Product> getPendingProducts() {
        return productRepository.findByStatus(ProductStatus.PENDING);
    }

    public List<Product> getApprovedProductsByUserId(Long userId) {
        return productRepository.findByUserIdAndStatus(userId, ProductStatus.APPROVED);
    }

    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    @Transactional
    public Product createProduct(ProductDto productDto, List<MultipartFile> images) {
        User user = userRepository.findById(productDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + productDto.getUserId()));

        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.SELLER))) {
            throw new IllegalStateException("User does not have SELLER role");
        }

        if (productRepository.existsByNameAndUserId(productDto.getName(), productDto.getUserId())) {
            throw new IllegalArgumentException("A product with this name already exists for this user");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setStatus(ProductStatus.PENDING);

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
        product.setCategory(category);

        product.setUser(user);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                try {
                    String imageUrl = cloudinaryService.uploadFile(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(savedProduct);
                    productImage.setImageUrl(imageUrl);
                    productImage.setCreatedAt(LocalDateTime.now());
                    productImage.setUpdatedAt(LocalDateTime.now());
                    productImageRepository.save(productImage);
                    savedProduct.addImage(productImage);
                } catch (Exception e) {
                    logger.error("Error processing image", e);
                }
            }
        }

        notifyAdmin(savedProduct);

        return savedProduct;
    }

    private void notifyAdmin(Product product) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@misakguambshop.com");
        message.setTo("plantillajs@gmail.com");
        message.setSubject("Nuevo producto pendiente de revisión - MisakGuambShop");
        message.setText(String.format(
                "Estimado Administrador,\n\n" +
                        "Un nuevo producto ha sido creado y está pendiente de revisión en MisakGuambShop.\n\n" +
                        "Detalles del producto:\n" +
                        "- ID: %d\n" +
                        "- Nombre: %s\n" +
                        "- Vendedor: %s\n" +
                        "- Categoría: %s\n" +
                        "- Precio: %.2f\n" +
                        "- Fecha de creación: %s\n\n" +
                        "Para revisar, aprobar o rechazar este producto, por favor acceda al panel de administración:\n" +
                        "%s/admin/products/pending/%d\n\n" +
                        "Gracias por su atención y dedicación a MisakGuambShop.\n\n" +
                        "Saludos,\n" +
                        "El equipo de MisakGuambShop",
                product.getId(),
                product.getName(),
                product.getUser().getUsername(),
                product.getCategory().getName(),
                product.getPrice(),
                product.getCreatedAt(),
                System.getenv("FRONTEND_URL"),
                product.getId()
        ));
        mailSender.send(message);
    }

    @Transactional
    public Product approveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));
        product.setStatus(ProductStatus.APPROVED);
        product.setUpdatedAt(LocalDateTime.now());
        Product approvedProduct = productRepository.save(product);
        productRepository.flush();
        notifySeller(approvedProduct, true);
        return approvedProduct;
    }

    @Transactional
    public Product rejectProduct(Long productId, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));
        product.setStatus(ProductStatus.REJECTED);
        product.setRejectionReason(reason);
        product.setUpdatedAt(LocalDateTime.now());
        Product rejectedProduct = productRepository.save(product);
        notifySeller(rejectedProduct, false);
        return rejectedProduct;
    }

    private void notifySeller(Product product, boolean approved) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@misakguambshop.com");
        message.setTo(product.getUser().getEmail());

        if (approved) {
            message.setSubject("¡Su producto ha sido aprobado! - MisakGuambShop");
            message.setText(String.format(
                    "Estimado/a %s,\n\n" +
                            "¡Nos complace informarle que su producto '%s' ha sido aprobado y ya está disponible en MisakGuambShop!\n\n" +
                            "Detalles del producto:\n" +
                            "- ID: %d\n" +
                            "- Nombre: %s\n" +
                            "- Categoría: %s\n" +
                            "- Precio: %.2f\n" +
                            "- Fecha de aprobación: %s\n\n" +
                            "Su producto ahora es visible para todos los clientes en nuestra tienda en línea. Le recomendamos que revise la lista completa de su producto para asegurarse de que toda la información es correcta.\n\n" +
                            "Recuerde mantener actualizada la información de su producto, especialmente el stock disponible.\n\n" +
                            "Gracias por contribuir a la diversidad y calidad de productos en MisakGuambShop. ¡Le deseamos mucho éxito con sus ventas!\n\n" +
                            "Si tiene alguna pregunta o necesita asistencia, no dude en contactarnos.\n\n" +
                            "Saludos cordiales,\n" +
                            "El equipo de MisakGuambShop",
                    product.getUser().getUsername(),
                    product.getName(),
                    product.getId(),
                    product.getName(),
                    product.getCategory().getName(),
                    product.getPrice(),
                    product.getUpdatedAt()
            ));
        } else {
            message.setSubject("Actualización sobre su producto - MisakGuambShop");
            message.setText(String.format(
                    "Estimado/a %s,\n\n" +
                            "Gracias por su reciente envío del producto '%s' a MisakGuambShop. Después de una cuidadosa revisión, lamentamos informarle que el producto no ha sido aprobado para su publicación en este momento.\n\n" +
                            "Detalles del producto:\n" +
                            "- ID: %d\n" +
                            "- Nombre: %s\n" +
                            "- Categoría: %s\n" +
                            "- Fecha de revisión: %s\n\n" +
                            "Motivo de la no aprobación:\n%s\n\n" +
                            "Le animamos a revisar y actualizar su producto teniendo en cuenta estos comentarios. Una vez realizadas las modificaciones necesarias, puede volver a enviar el producto para su revisión.\n\n" +
                            "Si tiene alguna pregunta sobre los comentarios recibidos o necesita aclaraciones adicionales, no dude en ponerse en contacto con nuestro equipo de soporte.\n\n" +
                            "Apreciamos su comprensión y su contribución continua a la comunidad de MisakGuambShop.\n\n" +
                            "Saludos cordiales,\n" +
                            "El equipo de MisakGuambShop",
                    product.getUser().getUsername(),
                    product.getName(),
                    product.getId(),
                    product.getName(),
                    product.getCategory().getName(),
                    product.getUpdatedAt(),
                    product.getRejectionReason()
            ));
        }

        mailSender.send(message);
    }

    public Product updateProduct(Long id, ProductDto productDto, List<MultipartFile> images) {
        Product product = getProductById(id);

        if (!product.getName().equals(productDto.getName()) &&
                productRepository.existsByNameAndUserId(productDto.getName(), product.getUser().getId())) {
            throw new IllegalArgumentException("A product with this name already exists for this user");
        }

        updateProductFromDto(product, productDto);

        if (images != null && !images.isEmpty()) {
            // Remove existing images
            product.getImages().clear();

            // Add new images
            for (MultipartFile image : images) {
                String imageUrl = cloudinaryService.uploadFile(image);
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageUrl(imageUrl);
                productImage.setCreatedAt(LocalDateTime.now());
                productImage.setUpdatedAt(LocalDateTime.now());
                product.addImage(productImage);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public Product patchProduct(Long id, ProductDto productDto, MultipartFile image) {
        Product product = getProductById(id);

        if (productDto.getName() != null && !productDto.getName().isEmpty()) {
            if (!product.getName().equals(productDto.getName()) &&
                    productRepository.existsByNameAndUserId(productDto.getName(), product.getUser().getId())) {
                throw new IllegalArgumentException("A product with this name already exists for this user");
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

        if (productDto.getUserId() != null) {
            User user = userRepository.findById(productDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + productDto.getUserId()));
            if (!user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.SELLER))) {
                throw new IllegalArgumentException("User is not a seller");
            }
            product.setUser(user);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image);
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imageUrl);
            productImage.setProduct(product);
            productImage.setCreatedAt(LocalDateTime.now());
            productImage.setUpdatedAt(LocalDateTime.now());
            productImageRepository.save(productImage);
            product.getImages().add(productImage);
        }

        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
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

        if (productDto.getUserId() != null) {
            User user = userRepository.findById(productDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + productDto.getUserId()));
            if (!user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.SELLER))) {
                throw new IllegalArgumentException("User is not a seller");
            }
            product.setUser(user);
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }
}
