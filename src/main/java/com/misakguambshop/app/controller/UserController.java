package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.exception.ErrorResponse;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import com.misakguambshop.app.model.Role;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateResetToken(@RequestParam("token") String token) {
        logger.info("Received reset token: {}", token);

        // Decode the token if it's URL-encoded
        String decodedToken;
        try {
            decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8.toString());
            logger.info("Decoded token: {}", decodedToken);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error decoding token: {}", e.getMessage());
            return new ResponseEntity<>("Error decoding token", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.validateResetToken(decodedToken);
            logger.info("Token valid");
            return new ResponseEntity<>("Token válido", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage());
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User newUser = userService.createUser(userDto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String message = userService.forgotPassword(email);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
    @PostMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<User> uploadProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        User updatedUser = userService.uploadProfileImage(id, file);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        if (token == null || newPassword == null) {
            return new ResponseEntity<>("Token and new password are required.", HttpStatus.BAD_REQUEST);
        }

        try {
            String message = userService.resetPassword(token, newPassword);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUser(id, userDto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reactivate")
    public ResponseEntity<?> reactivateAccount(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        System.out.println("Reactivando cuenta para el email: " + email);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            User user = userService.reactivateAccount(email);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Cuenta reactivada exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


    @PutMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<User> updateProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        User updatedUser = userService.updateProfileImage(id, file);
        return ResponseEntity.ok(updatedUser);
    }


    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Cuenta desactivada exitosamente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "El usuario no tiene una imagen de perfil para eliminar",
                        "user", user
                ));
            }

            User updatedUser = userService.deleteProfileImage(id);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "",
                    "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "No se pudo eliminar la imagen de perfil: " + e.getMessage()
            ));
        }
    }

    @PatchMapping("/{id}/become-seller")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> becomeSeller(@PathVariable Long id) {
        User updatedUser = userService.becomeSeller(id);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Usuario convertido a vendedor exitosamente",
                "user", Map.of(
                        "id", updatedUser.getId(),
                        "username", updatedUser.getUsername(),
                        "email", updatedUser.getEmail(),
                        "roles", updatedUser.getRoles().stream()
                                .map(role -> role.getName().toString())
                                .collect(Collectors.toList()),
                        "isSeller", updatedUser.getRoles().stream()
                                .anyMatch(role -> role.getName().equals(ERole.SELLER))
                )
        ));
    }
}