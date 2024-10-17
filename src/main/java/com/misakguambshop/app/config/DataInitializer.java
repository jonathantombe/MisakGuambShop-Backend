package com.misakguambshop.app.config;

import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.Role;
import com.misakguambshop.app.model.User;
import com.misakguambshop.app.repository.RoleRepository;
import com.misakguambshop.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();
        createAdminUser();
    }

    private void initializeRoles() {
        Arrays.stream(ERole.values())
                .forEach(role -> {
                    if (!roleRepository.existsByName(role)) {
                        roleRepository.save(new Role(role));
                    }
                });
    }

    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            Role adminRole = roleRepository.findByName(ERole.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setPhone("1234567890");
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            admin.setIsAdmin(true);

            User savedAdmin = userRepository.save(admin);
            System.out.println("Admin user created with isAdmin: " + savedAdmin.isAdmin());
        }
    }
}

