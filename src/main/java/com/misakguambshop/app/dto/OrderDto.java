package com.misakguambshop.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.misakguambshop.app.model.OrderStatus;
import com.misakguambshop.app.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDto {

    private Long id;

    @NotNull
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    @NotNull
    private OrderStatus status;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    @NotNull
    private PaymentStatus paymentStatus;

    private String paymentReference;


    public OrderDto() {
    }


    public OrderDto(Long id, Long userId, LocalDateTime orderDate, OrderStatus status, BigDecimal totalAmount) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
