package com.misakguambshop.app.repository;

import com.misakguambshop.app.model.ERole;
import com.misakguambshop.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}
