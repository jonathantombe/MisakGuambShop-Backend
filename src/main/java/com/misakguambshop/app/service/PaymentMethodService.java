package com.misakguambshop.app.service;

import com.misakguambshop.app.model.PaymentMethod;
import com.misakguambshop.app.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }

    public Optional<PaymentMethod> findById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    @Transactional
    public PaymentMethod create(PaymentMethod paymentMethod) {
        if (paymentMethodRepository.existsByName(paymentMethod.getName())) {
            throw new RuntimeException("Payment method name already exists");
        }
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public PaymentMethod update(Long id, PaymentMethod paymentMethod) {
        PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        // Solo actualizar si el nombre es diferente y no existe
        if (!existingPaymentMethod.getName().equals(paymentMethod.getName()) &&
                paymentMethodRepository.existsByName(paymentMethod.getName())) {
            throw new RuntimeException("Payment method name already exists");
        }

        existingPaymentMethod.setName(paymentMethod.getName());
        existingPaymentMethod.setStatus(paymentMethod.getStatus());

        return paymentMethodRepository.save(existingPaymentMethod);
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new RuntimeException("Payment method not found");
        }
        paymentMethodRepository.deleteById(id);
    }
}