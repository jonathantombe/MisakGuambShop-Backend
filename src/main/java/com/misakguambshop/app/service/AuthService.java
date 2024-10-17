package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.SellerSignupDto;
import com.misakguambshop.app.dto.UserDto;
import com.misakguambshop.app.dto.UserLoginDto;
import com.misakguambshop.app.dto.UserSignupDto;
import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.Role;
import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.RoleRepository;
import com.misakguambshop.app.repository.SellerRepository;
import com.misakguambshop.app.repository.UserRepository;
import com.misakguambshop.app.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;


@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SellerRepository sellerRepository;


    public String authenticateUser(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }

    public User registerUser(UserSignupDto signUpDto, ERole roleType) {
        if(userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        if(userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso");
        }

        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        User user = new User(
                signUpDto.getUsername(),
                signUpDto.getEmail(),
                passwordEncoder.encode(signUpDto.getPassword()),
                signUpDto.getPhone()
        );

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("no se encuentra el rol."));

        user.setRoles(Collections.singleton(role));
        user.setIsSeller(false);

        return userRepository.save(user);
    }

    public User activateAsSeller(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setIsSeller(true);
        Role sellerRole = roleRepository.findByName(ERole.SELLER)
                .orElseThrow(() -> new RuntimeException("Rol de vendedor no encontrado"));
        user.getRoles().add(sellerRole);

        return userRepository.save(user);
    }

    public Seller registerSeller(SellerSignupDto signUpDto, ERole roleType) {
        if(userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está en uso");
        }

        Seller seller = new Seller();
        seller.setFullName(signUpDto.getFullName());
        seller.setEmail(signUpDto.getEmail());
        seller.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        seller.setPhone(signUpDto.getPhone());
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("no se encuentra el rol."));

        seller.setRoles(Collections.singleton(role));

        return sellerRepository.save(seller);
    }
}
