package com.misakguambshop.app.service;

import com.misakguambshop.app.dto.PaymentDto;
import com.misakguambshop.app.model.Payment;
import com.misakguambshop.app.model.PaymentMethod;
import com.misakguambshop.app.model.PaymentStatus;
import com.misakguambshop.app.repository.PaymentMethodRepository;
import com.misakguambshop.app.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMethodRepository paymentMethodRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentMethodRepository paymentMethodRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public Payment createPayment(PaymentDto paymentDto) {
        // Buscar el método de pago
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDto.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        Payment payment = new Payment();
        payment.setOrderId(paymentDto.getOrderId());
        payment.setUserId(paymentDto.getUserId());
        payment.setAmount(paymentDto.getAmount());
        payment.setCurrency(paymentDto.getCurrency());
        payment.setPaymentMethod(paymentMethod);

        if (paymentDto.getPaymentStatus() != null) {
            payment.setPaymentStatus(PaymentStatus.valueOf(paymentDto.getPaymentStatus().toUpperCase()));
        } else {
            throw new IllegalArgumentException("El campo paymentStatus es obligatorio");
        }

        payment.setTransactionId(paymentDto.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setReceiptUrl(paymentDto.getReceiptUrl());
        payment.setRefundAmount(paymentDto.getRefundAmount());
        payment.setIpAddress(paymentDto.getIpAddress());

        return paymentRepository.save(payment);
    }


    public Optional<Payment> getPayment(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment updatePayment(Long id, PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Actualiza el monto
        payment.setAmount(paymentDto.getAmount());

        // Actualiza la moneda
        if (paymentDto.getCurrency() != null) {
            payment.setCurrency(paymentDto.getCurrency());
        }

        // Actualiza el estado de pago
        if (paymentDto.getPaymentStatus() != null) {
            payment.setPaymentStatus(PaymentStatus.valueOf(paymentDto.getPaymentStatus().toUpperCase()));
        }

        // Actualiza el método de pago si se proporciona un nuevo paymentMethodId
        if (paymentDto.getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDto.getPaymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
            payment.setPaymentMethod(paymentMethod);
        }

        // Actualiza otros campos
        if (paymentDto.getTransactionId() != null) {
            payment.setTransactionId(paymentDto.getTransactionId());
        }

        if (paymentDto.getReceiptUrl() != null) {
            payment.setReceiptUrl(paymentDto.getReceiptUrl());
        }

        if (paymentDto.getRefundAmount() != null) {
            payment.setRefundAmount(paymentDto.getRefundAmount());
        }

        if (paymentDto.getIpAddress() != null) {
            payment.setIpAddress(paymentDto.getIpAddress());
        }

        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }


    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}

