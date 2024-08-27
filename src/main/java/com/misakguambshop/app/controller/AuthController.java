package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.UserLoginDto;
import com.misakguambshop.app.dto.UserSignupDto;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.payload.JwtAuthenticationResponse;
import com.misakguambshop.app.repository.UserRepository;
import com.misakguambshop.app.security.JwtTokenProvider;
import com.misakguambshop.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/signup/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupDto signUpDto) {
        User user = authService.registerUser(signUpDto, ERole.ROLE_USER);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/signup/seller")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody UserSignupDto signUpDto) {
        User user = authService.registerUser(signUpDto, ERole.ROLE_SELLER);
        return ResponseEntity.ok("Seller registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}