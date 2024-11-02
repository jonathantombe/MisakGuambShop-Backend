package com.misakguambshop.app.dto;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    private Long id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private Long paymentMethodId;
    private String paymentStatus;
    private String transactionId;
    private String receiptUrl;
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    private String ipAddress;
    private LocalDateTime paymentDate;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public PaymentDto() {
    }

    // Constructor con todos los atributos
    public PaymentDto(Long id, Long orderId, Long userId, BigDecimal amount, String currency, Long paymentMethodId,
                      String paymentStatus, String transactionId, LocalDateTime paymentDate, LocalDateTime updatedAt,
                      String receiptUrl, BigDecimal refundAmount, LocalDateTime refundDate, String ipAddress) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethodId = paymentMethodId;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.paymentDate = paymentDate;
        this.updatedAt = updatedAt;
        this.receiptUrl = receiptUrl;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
        this.ipAddress = ipAddress;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
