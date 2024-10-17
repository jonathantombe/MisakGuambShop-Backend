package com.misakguambshop.app.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class SellerSignupDto {

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre de usuario solo puede contener letras y números.")
    private String fullName;

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

    private LocalDateTime passwordResetExpiration;

    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    // Constructor vacío
    public SellerSignupDto() {
    }

    // Constructor con parámetros
    public SellerSignupDto(String fullName, String email, String phone, String password, String confirmPassword) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // Getters y setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public LocalDateTime getPasswordResetExpiration() {
        return passwordResetExpiration;
    }

    public void setPasswordResetExpiration(LocalDateTime passwordResetExpiration) {
        this.passwordResetExpiration = passwordResetExpiration;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

}