package com.misakguambshop.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@Data
public class ShipmentDto {

    private Long id;

    @NotBlank(message = "La dirección es obligatoria.")
    @Pattern(regexp = "^.{5,}$", message = "La dirección debe tener al menos 5 caracteres.")
    private String address;

    @NotBlank(message = "El nombre del destinatario es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "El nombre del destinatario solo puede contener letras, números y espacios.")
    private String recipientName;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]{10}$", message = "El número de teléfono debe tener 10 dígitos exactos.")
    private String phoneNumber;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Por favor, ingrese un correo electrónico válido.")
    private String email;

    @NotBlank(message = "El departamento es obligatorio.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "El departamento solo puede contener letras y espacios.")
    private String department;

    @NotBlank(message = "La ciudad es obligatoria.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "La ciudad solo puede contener letras y espacios.")
    private String city;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "El barrio solo puede contener letras y espacios.")
    private String neighborhood;

    @NotBlank(message = "El código postal es obligatorio.")
    @Pattern(regexp = "^[0-9]{4,}$", message = "El código postal debe contener al menos 4 dígitos.")
    private String postalCode;

    @Size(max = 500, message = "Las notas de envío no pueden superar los 500 caracteres.")
    private String shippingNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
