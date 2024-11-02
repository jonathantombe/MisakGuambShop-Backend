package com.misakguambshop.app.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    private Long orderId;
    private Long userId;
    private String reference;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String paymentMethod;
    private String ipAddress;
    private String buyerEmail;
    private String buyerName;
    private String buyerDocument;
    private String buyerPhone;

    // Información de la tarjeta de crédito
    private String creditCardNumber;
    private String creditCardExpirationDate; // Formato: YYYY/MM
    private String creditCardSecurityCode;
    private String creditCardName;

    // Información de la dirección de facturación
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingCountry;
    private String billingPostalCode;
}