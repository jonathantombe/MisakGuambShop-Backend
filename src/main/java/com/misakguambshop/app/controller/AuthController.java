package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.SellerSignupDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
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

    @PostMapping("/signup/user")
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

            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (BadCredentialsException e) {
            logger.error("Error durante la autenticación: Credenciales incorrectas", e);
            return ResponseEntity.badRequest().body("Las credenciales ingresadas no son correctas. Por favor, verifica tu correo electrónico y contraseña..");
        } catch (Exception e) {
            logger.error("Error inesperado durante la autenticación: ", e);
            return ResponseEntity.badRequest().body("Error de autenticación: " + e.getMessage());
        }
    }
}