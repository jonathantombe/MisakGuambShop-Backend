package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.UserPaymentMethodDto;
import com.misakguambshop.app.model.UserPaymentMethod;
import com.misakguambshop.app.service.UserPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-payment-methods")
public class UserPaymentMethodController {

    private final UserPaymentMethodService paymentMethodService;

    public UserPaymentMethodController(UserPaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @Autowired
    private UserPaymentMethodService userPaymentMethodService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserPaymentMethod> getAllPaymentMethods() {
        return userPaymentMethodService.getAllPaymentMethods();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'SELLER')")
    public ResponseEntity<UserPaymentMethod> getPaymentMethodById(@PathVariable Long id) {
        return userPaymentMethodService.getPaymentMethodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('USER', 'SELLER')")
    public ResponseEntity<?> getPaymentMethodsByUserId(@PathVariable Long userId) {
        try {
            // Validar que userId no sea nulo
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body("El ID de usuario no puede ser nulo");
            }

            List<UserPaymentMethod> paymentMethods = userPaymentMethodService.getPaymentMethodsByUserId(userId);

            if (paymentMethods.isEmpty()) {
                return ResponseEntity.ok()
                        .body("No se encontraron métodos de pago para el usuario con ID: " + userId);
            }

            return ResponseEntity.ok(paymentMethods);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body("ID de usuario inválido: debe ser un número");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al obtener los métodos de pago: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'SELLER')")
    public ResponseEntity<?> createPaymentMethod(@Validated @RequestBody UserPaymentMethodDto paymentMethodDto) {
        try {
            // Convertir DTO a entidad
            UserPaymentMethod paymentMethod = new UserPaymentMethod();
            paymentMethod.setUserId(paymentMethodDto.getUserId());
            paymentMethod.setPaymentMethodId(paymentMethodDto.getPaymentMethodId().intValue());
            paymentMethod.setCardToken(paymentMethodDto.getCardToken());
            paymentMethod.setLastFourDigits(paymentMethodDto.getLastFourDigits());
            paymentMethod.setCardBrand(paymentMethodDto.getCardBrand());
            paymentMethod.setCardholderName(paymentMethodDto.getCardholderName());
            paymentMethod.setExpirationDate(paymentMethodDto.getExpirationDate());
            paymentMethod.setIsDefault(paymentMethodDto.getIsDefault());

            UserPaymentMethod created = userPaymentMethodService.createPaymentMethod(paymentMethod);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al crear el método de pago: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<UserPaymentMethod> updatePaymentMethod(
            @PathVariable Long id, @RequestBody UserPaymentMethod updatedPaymentMethod) {
        try {
            UserPaymentMethod updated = userPaymentMethodService.updatePaymentMethod(id, updatedPaymentMethod);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id) {
        boolean isDeleted = paymentMethodService.deletePaymentMethodById(id);

        if (isDeleted) {
            return ResponseEntity.ok("Método de pago eliminado exitosamente.");
        } else {
            return ResponseEntity.status(404).body("Método de pago no encontrado.");
        }
    }
}
