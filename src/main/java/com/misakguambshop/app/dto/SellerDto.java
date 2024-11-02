package com.misakguambshop.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;

@Data
public class SellerDto {

    private Long id;

    private Long userId;

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

    @NotNull(message = "La imagen de perfil es obligatoria")
    private MultipartFile profileImage;

    @Pattern(regexp = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$",
            message = "La URL de la imagen de perfil debe ser una URL válida")
    @Size(max = 255, message = "La URL de la imagen de perfil no debe exceder los 255 caracteres")
    private String profileImageUrl;

    private boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean isPasswordConfirmed() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isValidProfileImage() throws IOException {
        if (profileImage == null || profileImage.isEmpty()) {
            return false; // La imagen es obligatoria
        }

        // Validar tamaño
        if (profileImage.getSize() > 5 * 1024 * 1024) {
            return false; // La imagen excede 5MB
        }

        String contentType = profileImage.getContentType();
        if (contentType == null || !(contentType.equals("image/png") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg"))) {
            return false;
        }

        // Validar dimensiones
        BufferedImage img = ImageIO.read(profileImage.getInputStream());
        int width = img.getWidth();
        int height = img.getHeight();
        if (width < 200 || width > 1024 || height < 200 || height > 1024) {
            return false;
        }

        // La validación de contenido explícito se implementaría aquí

        return true;
    }
}