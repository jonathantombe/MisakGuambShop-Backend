package com.misakguambshop.app.repository;

import com.misakguambshop.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Métodos personalizados si es necesario
}
