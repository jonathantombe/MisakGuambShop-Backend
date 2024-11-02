package com.misakguambshop.app.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;

public class OrderDetailsDto {

    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long orderId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.00", inclusive = false, message = "El precio unitario debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio unitario debe tener máximo dos decimales")
    private BigDecimal unitPrice;

    private BigDecimal subtotal;


    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
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

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
