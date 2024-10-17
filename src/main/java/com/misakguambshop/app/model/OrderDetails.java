package com.misakguambshop.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.00", inclusive = false, message = "El precio unitario debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio unitario debe tener máximo dos decimales")
    private BigDecimal unitPrice;

    @NotNull(message = "El subtotal debe ser calculado automáticamente")
    @DecimalMin(value = "0.00", inclusive = false, message = "El subtotal debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El subtotal debe tener máximo dos decimales")
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }


    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
