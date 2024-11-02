package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.SellerSignupDto;
import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.dto.UserLoginDto;
import com.misakguambshop.app.dto.UserSignupDto;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.payload.JwtAuthenticationResponse;
import com.misakguambshop.app.repository.UserRepository;
import com.misakguambshop.app.security.JwtAuthenticationFilter;
import com.misakguambshop.app.security.JwtTokenProvider;
import com.misakguambshop.app.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupDto signUpDto) {
        try {
            User user = authService.registerUser(signUpDto, ERole.USER);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al registrar al usuario: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/activate-seller")
    public ResponseEntity<?> activateAsSeller(@RequestParam Long userId) {
        try {
            User updatedUser = authService.activateAsSeller(userId);
            return ResponseEntity.ok("Usuario activado como vendedor exitosamente");
        } catch (Exception e) {
            logger.error("Error al activar al usuario como vendedor: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/signup/seller")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody SellerSignupDto signUpDto) {
        logger.info("Solicitud de registro de vendedor recibida: {}", signUpDto);
        try {
            Seller seller = authService.registerSeller(signUpDto, ERole.SELLER);
            return ResponseEntity.ok("Vendedor registrado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al registrar al vendedor: ", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmailWithRoles(loginDto.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginDto.getEmail()));

            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "La cuenta está desactivada", "status", "account_deactivated"));
            }

            String jwt = tokenProvider.generateToken(authentication);

            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            userDto.setPhone(user.getPhone());
            userDto.setIsActive(user.getIsActive());
            userDto.setIsAdmin(user.isAdmin());
            userDto.setIsSeller(user.getIsSeller());
            userDto.setProfileImageUrl(user.getProfileImageUrl());

            // Mapear los roles
            Set<String> roles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());
            userDto.setRoles(new ArrayList<>(roles));

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userDto));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Las credenciales ingresadas no son correctas.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error de autenticación: " + e.getMessage());
        }
    }
}