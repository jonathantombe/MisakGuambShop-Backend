package com.misakguambshop.app.security;

import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.SellerRepository;
import com.misakguambshop.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Primero, busca en la tabla de usuarios
        User user = userRepository.findByEmailWithRoles(email)
                .orElse(null);

        if (user != null) {
            return UserPrincipal.create(user);
        }

        // Si no se encuentra en la tabla de usuarios, busca en la tabla de vendedores
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        return UserPrincipal.create(seller);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        // Primero, busca en la tabla de usuarios
        User user = userRepository.findById(id)
                .orElse(null);

        if (user != null) {
            return UserPrincipal.create(user);
        }

        // Si no se encuentra en la tabla de usuarios, busca en la tabla de vendedores
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));

        return UserPrincipal.create(seller);
    }
}