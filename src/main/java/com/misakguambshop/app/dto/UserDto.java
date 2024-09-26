package com.misakguambshop.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre de usuario solo puede contener letras y números.")
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Por favor, ingrese un correo electrónico válido.")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$", message = "El correo electrónico debe estar en minúsculas y tener un formato válido.")
    private String email;

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]*$", message = "¡Número de teléfono no válido!")
    @Size(min = 10, max = 10, message = "El número de teléfono debe tener exactamente 10 dígitos.")
    private String phone;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial.")
    private String password;

    @NotBlank(message = "Debe confirmar su contraseña.")
    private String confirmPassword;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    // Valida si la confirmación de la contraseña coincide con la contraseña
    public boolean isPasswordConfirmed() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }
}
