package com.misakguambshop.app.service;

import com.misakguambshop.app.model.UserPaymentMethod;
import com.misakguambshop.app.repository.UserPaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserPaymentMethodService {

    private final UserPaymentMethodRepository paymentMethodRepository;

    @Autowired
    public UserPaymentMethodService(UserPaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Autowired
    private UserPaymentMethodRepository userPaymentMethodRepository;

    public List<UserPaymentMethod> getAllPaymentMethods() {
        return userPaymentMethodRepository.findAll();
    }

    public Optional<UserPaymentMethod> getPaymentMethodById(Long id) {
        return userPaymentMethodRepository.findById(id);
    }

    public List<UserPaymentMethod> getPaymentMethodsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }
        return userPaymentMethodRepository.findByUserId(userId);
    }

    public UserPaymentMethod createPaymentMethod(UserPaymentMethod paymentMethod) {
        // Validación adicional
        if (paymentMethod.getCardBrand() == null || paymentMethod.getCardBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("La marca de la tarjeta no puede estar vacía");
        }
        return userPaymentMethodRepository.save(paymentMethod);
    }

    public UserPaymentMethod updatePaymentMethod(Long id, UserPaymentMethod updatedPaymentMethod) {
        return userPaymentMethodRepository.findById(id).map(existingMethod -> {
            existingMethod.setCardToken(updatedPaymentMethod.getCardToken());
            existingMethod.setLastFourDigits(updatedPaymentMethod.getLastFourDigits());
            existingMethod.setCardBrand(updatedPaymentMethod.getCardBrand());
            existingMethod.setCardholderName(updatedPaymentMethod.getCardholderName());
            existingMethod.setExpirationDate(updatedPaymentMethod.getExpirationDate());
            existingMethod.setIsDefault(updatedPaymentMethod.getIsDefault());
            return userPaymentMethodRepository.save(existingMethod);
        }).orElseThrow(() -> new RuntimeException("Payment method not found"));
    }

    public void deletePaymentMethod(Long id) {
        userPaymentMethodRepository.deleteById(id);
    }

    public boolean deletePaymentMethodById(Long id) {
        if (paymentMethodRepository.existsById(id)) {
            paymentMethodRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

