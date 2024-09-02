package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.SellerSignupDto;
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
        if(userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User(
                signUpDto.getUsername(),
                signUpDto.getEmail(),
                passwordEncoder.encode(signUpDto.getPassword()),
                signUpDto.getPhone()
        );

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRoles(Collections.singleton(role));

        return userRepository.save(user);
    }

    public Seller registerSeller(SellerSignupDto signUpDto, ERole roleType) {
        if(userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        Seller seller = new Seller();
        seller.setFullName(signUpDto.getFullName());
        seller.setEmail(signUpDto.getEmail());
        seller.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        seller.setPhone(signUpDto.getPhone());
        seller.setCompanyName(signUpDto.getCompanyName());
        seller.setDescription(signUpDto.getDescription());
        seller.setCity(signUpDto.getCity());

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        seller.setRoles(Collections.singleton(role));

        return sellerRepository.save(seller);
    }
}
