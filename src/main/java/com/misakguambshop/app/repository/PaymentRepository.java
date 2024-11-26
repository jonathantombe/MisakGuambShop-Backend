package com.misakguambshop.app.repository;

import com.misakguambshop.app.model.Payment;
import com.misakguambshop.app.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Método para buscar pagos por ID de transacción
    Optional<Payment> findByTransactionId(String transactionId);

}
