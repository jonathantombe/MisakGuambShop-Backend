package com.misakguambshop.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;


@Data
public class UserPaymentMethodDto {

    @NotNull(message = "El ID de usuario no puede ser nulo")
    @Min(value = 1, message = "El ID de usuario debe ser mayor que 0")
    private Long userId;

    @NotNull(message = "El ID del método de pago no puede ser nulo")
    @Min(value = 1, message = "El ID del método de pago debe ser mayor que 0")
    private Long paymentMethodId;

    @NotBlank(message = "El token de la tarjeta no puede estar vacío")
    private String cardToken;

    @NotBlank(message = "Los últimos cuatro dígitos no pueden estar vacíos")
    @Size(min = 4, max = 4, message = "Los últimos cuatro dígitos deben tener exactamente 4 caracteres")
    private String lastFourDigits;

    @NotBlank(message = "La marca de la tarjeta no puede estar vacía")
    private String cardBrand;

    @NotBlank(message = "El nombre del titular de la tarjeta no puede estar vacío")
    private String cardholderName;

    @NotBlank(message = "La fecha de expiración no puede estar vacía")
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{4}", message = "La fecha de expiración debe estar en el formato MM/YYYY")
    private String expirationDate;

    private Boolean isDefault;

}


