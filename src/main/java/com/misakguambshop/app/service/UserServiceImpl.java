package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.exception.ResourceNotFoundException;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

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

        // Solo actualiza la contraseña si está presente y no vacía
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            // Validar si las contraseñas coinciden antes de actualizar
            if (!userDto.isPasswordConfirmed()) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setPhone(userDto.getPhone());
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
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("La cuenta ya está desactivada.");
        }
        user.setIsActive(false);  // Cambiar el estado de la cuenta a desactivada
        userRepository.save(user);
    }

    @Override
    public String requestReactivation(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró una cuenta con ese correo electrónico."));

        if (user.getIsActive()) {
            throw new IllegalArgumentException("La cuenta ya está activa.");
        }

        // Generate a unique token
        String token = UUID.randomUUID().toString();
        user.setReactivationToken(token);
        userRepository.save(user);

        // Send reactivation email
        sendReactivationEmail(user.getEmail(), token);

        return "Se ha enviado un enlace de reactivación a tu correo electrónico.";
    }

    private void sendReactivationEmail(String email, String token) {
        String reactivationUrl = "http://localhost:3000/reactivate-account?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@misakguambshop.com");
        message.setTo(email);
        message.setSubject("Reactivación de cuenta - MisakGuambShop");
        message.setText(String.format(
                "Estimado usuario,\n\n" +
                        "Hemos recibido una solicitud para reactivar tu cuenta en MisakGuambShop.\n\n" +
                        "Para reactivar tu cuenta, por favor haz clic en el siguiente enlace o cópialo y pégalo en tu navegador:\n" +
                        "%s\n\n" +
                        "Este enlace expirará en 24 horas por razones de seguridad.\n\n" +
                        "Si no has solicitado la reactivación de tu cuenta, por favor ignora este correo electrónico o contacta con nuestro equipo de soporte.\n\n" +
                        "Gracias por ser parte de la comunidad MisakGuambShop.\n\n" +
                        "Saludos,\n" +
                        "El equipo de MisakGuambShop",
                reactivationUrl
        ));

        mailSender.send(message);
    }

    @Override
    public String reactivateAccount(String token) {
        User user = userRepository.findByReactivationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de reactivación inválido."));

        if (user.getIsActive()) {
            throw new IllegalArgumentException("La cuenta ya está activa.");
        }

        user.setIsActive(true);
        user.setReactivationToken(null);
        userRepository.save(user);

        return "Cuenta reactivada con éxito.";
    }

    @Override
    public String forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no está registrado.");
        }
        User user = userOptional.get();

        // Generar un token único y establecer la fecha de expiración
        String token = UUID.randomUUID().toString();
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis() + 3600000); // Token expira en 1 hora
        user.setPasswordResetToken(token);
        user.setPasswordResetExpiration(expirationDate);
        userRepository.save(user);

        // Enviar el correo electrónico al usuario con el enlace de restablecimiento
        sendPasswordResetEmail(user.getEmail(), token);

        return "Se ha enviado un enlace de restablecimiento de contraseña a tu correo.";
    }

    private void sendPasswordResetEmail(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        String resetUrl = "http://localhost:3000/reset-password?token=" + token;

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

        mailSender.send(message);
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByPasswordResetToken(token);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("El enlace de restablecimiento es inválido o ha expirado.");
        }
        User user = userOptional.get();

        // Verificar si el token no ha expirado
        if (user.getPasswordResetExpiration().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("El enlace de restablecimiento ha expirado.");
        }

        // Actualizar la contraseña del usuario
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiration(null);
        userRepository.save(user);

        // Enviar correo de confirmación
        sendPasswordChangeConfirmationEmail(user.getEmail());

        return "¡Contraseña actualizada correctamente!";
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
}
