package com.misakguambshop.app.repository;

import com.misakguambshop.app.model.UserPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, Long> {
    List<UserPaymentMethod> findByUserId(Long userId);
}
