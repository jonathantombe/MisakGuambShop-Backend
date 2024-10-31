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
    // Método para encontrar pagos por estado
    List<Payment> findByPaymentStatus(PaymentStatus status);

    // Método para encontrar pagos de un usuario específico
    List<Payment> findByUserId(Long userId);

    // Método para encontrar pagos dentro de un rango de montos
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // Método para buscar pagos por ID de transacción
    Optional<Payment> findByTransactionId(String transactionId);

    // Método para obtener todos los pagos realizados con un método de pago específico
    List<Payment> findByPaymentMethodId(Long paymentMethodId);
}
