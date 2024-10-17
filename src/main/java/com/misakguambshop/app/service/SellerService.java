package com.misakguambshop.app.service;

import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.repository.SellerRepository;
import com.misakguambshop.app.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Optional<Seller> getSellerById(Long id) {
        return sellerRepository.findById(id);
    }

    public Seller updateSeller(Long id, Seller sellerDetails) {
        return sellerRepository.findById(id)
                .map(existingSeller -> {
                    existingSeller.setFullName(sellerDetails.getFullName());
                    existingSeller.setEmail(sellerDetails.getEmail());
                    existingSeller.setPhone(sellerDetails.getPhone());
                    if (sellerDetails.getPassword() != null && !sellerDetails.getPassword().isEmpty()) {
                        existingSeller.setPassword(passwordEncoder.encode(sellerDetails.getPassword()));
                    }

                    return sellerRepository.save(existingSeller);
                })
                .orElseGet(() -> null);
    }

    public void deleteSeller(Long id) {
        sellerRepository.deleteById(id);
    }
}