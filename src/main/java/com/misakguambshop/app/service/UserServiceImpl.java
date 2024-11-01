package com.misakguambshop.app.service;

import com.misakguambshop.app.controller.UserController;
import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.Role;
import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.RoleRepository;
import com.misakguambshop.app.repository.SellerRepository;
import com.misakguambshop.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Override
    public User createUser(UserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhone(userDto.getPhone());

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Este bloque catch es por si acaso ocurre una violación de integridad no capturada por las comprobaciones anteriores
            throw new IllegalArgumentException("Error al crear el usuario: " + e.getMessage());
        }
    }


    @Transactional
    public User activateAsSeller(Long userId) {
        User user = getUserById(userId);
        if (user.getIsSeller()) {
            throw new IllegalStateException("El usuario ya es un vendedor.");
        }

        user.setIsSeller(true);

        // Opcional: Agregar el rol de vendedor si se maneja con roles
        Role sellerRole = roleRepository.findByName(ERole.SELLER)
                .orElseThrow(() -> new RuntimeException("Rol de vendedor no encontrado"));
        user.getRoles().add(sellerRole);

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllWithRoles();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        User user = getUserById(id);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            if (!userDto.isPasswordConfirmed()) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setPhone(userDto.getPhone());

        // Actualizar el estado de vendedor si es necesario
        if (userDto.getIsSeller() != user.getIsSeller()) {
            user.setIsSeller(userDto.getIsSeller());
            // Aquí podrías manejar la lógica de roles si es necesario
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deactivateUser(Long id) {  // Cambiado de deactivateAccount a deactivateUser
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public User reactivateAccount(String email) throws RuntimeException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setIsActive(true);
        return userRepository.save(user);
    }


    @Override
    public String forgotPassword(String email) {
        logger.info("Iniciando proceso de olvido de contraseña para el email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("Email no encontrado: {}", email);
            throw new IllegalArgumentException("El correo electrónico no está registrado.");
        }
        User user = userOptional.get();

        String token = UUID.randomUUID().toString();
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis() + 3600000 * 24);
        user.setPasswordResetToken(token);
        user.setPasswordResetExpiration(expirationDate);
        userRepository.save(user);
        logger.info("Token generado y guardado para el usuario: {}", user.getUsername());

        sendPasswordResetEmail(user.getEmail(), token);
        logger.info("Correo de restablecimiento enviado a: {}", email);

        return "Se ha enviado un enlace de restablecimiento de contraseña a tu correo.";
    }

    private void sendPasswordResetEmail(String email, String token) {
        logger.info("Preparando correo de restablecimiento para: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        // Usa baseUrl para construir la URL de restablecimiento de contraseña
        String resetUrl = baseUrl + "/reset-password?token=" + token; // URL ahora dinámica

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@misakguambshop.com");
        message.setTo(email);
        message.setSubject("Restablecer tu contraseña de MisakGuambShop");
        message.setText(String.format(
                "Hola %s,\n\n" +
                        "Hemos recibido tu solicitud para restablecer la contraseña de MisakGuambShop.\n\n" +
                        "Restablece tu contraseña aquí o pega el enlace de abajo en tu navegador:\n" +
                        "%s\n\n" +
                        "Este enlace expirará en 1 hora.\n\n" +
                        "Si no solicitaste un restablecimiento de contraseña, ignora este correo electrónico.\n\n" +
                        "Saludos,\n" +
                        "El equipo de MisakGuambShop",
                user.getUsername(), resetUrl
        ));

        try {
            mailSender.send(message);
            logger.info("Correo enviado exitosamente a: {}", email);
        } catch (Exception e) {
            logger.error("Error al enviar correo a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error al enviar el correo de restablecimiento", e);
        }
    }

    @Override
    public void validateResetToken(String token) {
        logger.info("Validating reset token: {}", token);
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> {
                    logger.warn("No user found for token: {}", token);
                    return new IllegalArgumentException("El enlace de restablecimiento es inválido o ha expirado.");
                });

        if (user.getPasswordResetExpiration() == null) {
            logger.warn("Reset expiration is null for user: {}", user.getEmail());
            throw new IllegalArgumentException("El enlace de restablecimiento es inválido.");
        }

        if (user.getPasswordResetExpiration().before(new Timestamp(System.currentTimeMillis()))) {
            logger.warn("Reset token expired for user: {}", user.getEmail());
            throw new IllegalArgumentException("El enlace de restablecimiento ha expirado.");
        }

        logger.info("Reset token is valid for user: {}", user.getEmail());
    }


    // Método de prueba para la conexión SMTP
    public void testSmtpConnection() {
        try {
            mailSender.send(new SimpleMailMessage());  // Enviamos un mensaje vacío como prueba
            logger.info("Conexión SMTP exitosa");
        } catch (Exception e) {
            logger.error("Error en la conexión SMTP: ", e);
        }
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        logger.info("Attempting to reset password with token: {}", token);
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> {
                    logger.warn("Invalid reset token: {}", token);
                    return new IllegalArgumentException("El enlace de restablecimiento es inválido o ha expirado.");
                });

        if (user.getPasswordResetExpiration().before(new Timestamp(System.currentTimeMillis()))) {
            logger.warn("Expired reset token for user: {}", user.getEmail());
            throw new IllegalArgumentException("El enlace de restablecimiento ha expirado.");
        }

        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetExpiration(null);
            userRepository.save(user);
            logger.info("Password reset successful for user: {}", user.getEmail());

            sendPasswordChangeConfirmationEmail(user.getEmail());

            return "¡Contraseña actualizada correctamente!";
        } catch (Exception e) {
            logger.error("Error resetting password for user: {}", user.getEmail(), e);
            throw new RuntimeException("Error al restablecer la contraseña. Por favor, inténtalo de nuevo más tarde.");
        }
    }

    private void sendPasswordChangeConfirmationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@misakguambshop.com");
        message.setTo(email);
        message.setSubject("Confirmación de cambio de contraseña - MisakGuambShop");
        message.setText(String.format(
                "Hola %s,\n\n" +
                        "Te confirmamos que la contraseña de tu cuenta en MisakGuambShop ha sido cambiada exitosamente.\n\n" +
                        "Detalles de la acción:\n" +
                        "- Fecha y hora: %s\n" +
                        "- Acción: Cambio de contraseña\n" +
                        "- Cuenta: %s\n\n" +
                        "Si realizaste esta acción, no necesitas hacer nada más. Ya puedes iniciar sesión con tu nueva contraseña.\n\n" +
                        "Si NO realizaste este cambio, por favor:\n" +
                        "1. Accede inmediatamente a tu cuenta de MisakGuambShop\n" +
                        "2. Cambia tu contraseña\n" +
                        "3. Contacta con nuestro equipo de soporte en support@misakguambshop.com\n\n" +
                        "Para tu seguridad, te recomendamos:\n" +
                        "- Usar contraseñas únicas y fuertes para cada una de tus cuentas en línea\n" +
                        "- Activar la autenticación de dos factores si está disponible\n" +
                        "- Nunca compartir tus credenciales de acceso\n\n" +
                        "Si tienes alguna pregunta o inquietud, no dudes en contactarnos.\n\n" +
                        "Gracias por ser parte de la comunidad MisakGuambShop.\n\n" +
                        "Saludos,\n" +
                        "El equipo de Seguridad de MisakGuambShop",
                user.getUsername(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                user.getEmail()
        ));

        mailSender.send(message);
    }

    @Override
    @Transactional
    public User uploadProfileImage(Long id, MultipartFile file) {
        User user = getUserById(id);
        if (user.getProfileImageUrl() != null) {
            logger.warn("El usuario ya tiene una imagen de perfil. Se procederá a actualizarla.");
        }
        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file);
            logger.info("Imagen subida a Cloudinary. URL: {}", imageUrl);
            user.setProfileImageUrl(imageUrl);
            User savedUser = userRepository.save(user);
            logger.info("Usuario guardado con URL de imagen de perfil: {}", savedUser.getProfileImageUrl());
            return savedUser;
        }
        throw new IllegalArgumentException("No se ha proporcionado una imagen válida.");
    }

    @Override
    @Transactional
    public User updateProfileImage(Long id, MultipartFile file) {
        User user = getUserById(id);
        if (file != null && !file.isEmpty()) {
            if (user.getProfileImageUrl() != null) {
                cloudinaryService.deleteFile(user.getProfileImageUrl());
                logger.info("Imagen de perfil anterior eliminada de Cloudinary");
            }
            String imageUrl = cloudinaryService.uploadFile(file);
            logger.info("Nueva imagen subida a Cloudinary. URL: {}", imageUrl);
            user.setProfileImageUrl(imageUrl);
            User savedUser = userRepository.save(user);
            logger.info("Usuario actualizado con nueva URL de imagen de perfil: {}", savedUser.getProfileImageUrl());
            return savedUser;
        }
        throw new IllegalArgumentException("No se ha proporcionado una imagen válida.");
    }

    @Override
    @Transactional
    public User deleteProfileImage(Long id) {
        User user = getUserById(id);
        if (user.getProfileImageUrl() != null) {
            try {
                cloudinaryService.deleteFile(user.getProfileImageUrl());
                logger.info("Imagen de perfil eliminada de Cloudinary");
                user.setProfileImageUrl(null);
                User savedUser = userRepository.save(user);
                logger.info("URL de imagen de perfil eliminada del usuario");
                return savedUser;
            } catch (Exception e) {
                logger.error("Error al eliminar la imagen de perfil de Cloudinary", e);
                // Incluso si hay un error al eliminar de Cloudinary, eliminamos la referencia en el usuario
                user.setProfileImageUrl(null);
                User savedUser = userRepository.save(user);
                logger.info("URL de imagen de perfil eliminada del usuario a pesar del error en Cloudinary");
                return savedUser;
            }
        } else {
            logger.info("El usuario no tiene una imagen de perfil para eliminar");
            return user; // Retornamos el usuario sin cambios
        }
    }

    @Transactional
    public User becomeSeller(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Role sellerRole = roleRepository.findByName(ERole.SELLER)
                .orElseThrow(() -> new RuntimeException("Rol SELLER no encontrado"));

        if (!user.getRoles().contains(sellerRole)) {
            user.getRoles().add(sellerRole);
            user = userRepository.save(user);
        }

        return user;
    }
}
