package com.misakguambshop.app.dto;

import com.misakguambshop.app.model.ShipmentStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ShipmentDto {

    private Long id;

    @NotNull
    private Long orderId;

    @NotBlank
    @Size(min = 10, max = 255)
    private String address;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\sáéíóúÁÉÍÓÚñÑ]+$", message = "El nombre solo puede contener letras, espacios y caracteres especiales")
    private String recipientName;

    @NotBlank
    @Pattern(regexp = "^(\\+\\d{1,3})?[\\d\\s-]{7,15}$", message = "El número de teléfono debe ser válido y contener entre 7 y 15 caracteres")
    private String phoneNumber;

    @Email
    private String email;

    @NotBlank
    private String shippingMethod;

    @NotNull
    private LocalDate shippingDate;

    @NotNull
    private LocalDate estimatedDeliveryDate;

    private LocalDate actualDeliveryDate;

    @NotBlank
    private String country;

    @NotBlank
    private String city;

    @NotNull
    private ShipmentStatus status;

    private BigDecimal weight;
    private BigDecimal shippingCost;
    private BigDecimal insuranceCost;

    @NotBlank(message = "El nombre de la empresa de envío es obligatorio")
    private String shippingCompany;



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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(BigDecimal insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
}