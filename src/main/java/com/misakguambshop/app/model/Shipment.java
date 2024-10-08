package com.misakguambshop.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long orderId;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 10, max = 255, message = "La dirección debe tener entre 10 y 255 caracteres")
    private String address;

    @NotBlank(message = "El nombre del destinatario es obligatorio")
    @Pattern(regexp = "^[a-zA-Z\\sáéíóúÁÉÍÓÚñÑ]+$", message = "El nombre solo puede contener letras, espacios y caracteres especiales")
    private String recipientName;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^(\\+\\d{1,3})?[\\d\\s-]{7,15}$", message = "El número de teléfono debe ser válido y contener entre 7 y 15 caracteres")
    private String phoneNumber;

    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;

    @NotBlank(message = "El método de envío es obligatorio")
    private String shippingMethod;

    @NotNull(message = "La fecha de envío es obligatoria")
    private LocalDate shippingDate;

    @NotNull(message = "La fecha de entrega estimada es obligatoria")
    private LocalDate estimatedDeliveryDate;

    private LocalDate actualDeliveryDate;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El estado del envío es obligatorio")
    private ShipmentStatus status;

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0")
    private BigDecimal weight;

    @DecimalMin(value = "0.0", inclusive = false, message = "El costo de envío debe ser mayor a 0")
    private BigDecimal shippingCost;

    private BigDecimal insuranceCost;

    @NotBlank(message = "El nombre de la empresa de envío es obligatorio")
    private String shippingCompany;

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