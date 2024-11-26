package com.misakguambshop.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.misakguambshop.app.config.WompiConfig;
import com.misakguambshop.app.controller.UserController;
import com.misakguambshop.app.dto.PaymentDto;
import com.misakguambshop.app.model.Payment;
import com.misakguambshop.app.model.PaymentStatus;
import com.misakguambshop.app.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets; // IMPORTADO
import java.security.MessageDigest; // IMPORTADO
import java.security.NoSuchAlgorithmException; // IMPORTADO
import java.time.LocalDateTime;
import java.util.Base64; // IMPORTADO
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${epayco.private-key}")
    private String epaycoPrivateKey;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public PaymentStatus getPaymentStatus(String referenceCode) {
        // Aquí iría la lógica para obtener el estado del pago de la base de datos o API externa.
        // Simulación de ejemplo:
        logger.info("Fetching payment status for reference code: {}", referenceCode);
        return PaymentStatus.PENDING; // Cambia esto según tu lógica.
    }

    public boolean validateSignature(Map<String, Object> payload) {
        try {
            String receivedSignature = (String) payload.get("x_signature");
            String referenceCode = (String) payload.get("x_ref_payco");
            String amount = (String) payload.get("x_amount");
            String currency = (String) payload.get("x_currency_code");

            // Concatenate values in the correct order
            String baseString = String.format("%s^%s^%s^%s",
                    epaycoPrivateKey,
                    referenceCode,
                    amount,
                    currency
            );

            // Generate SHA-256 hash
            String calculatedSignature = generateSHA256Hash(baseString);

            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            logger.error("Error validating signature", e);
            return false;
        }
    }

    public PaymentStatus processPaymentConfirmation(
            String referenceCode,
            String transactionState,
            String orderId) {

        PaymentStatus status = mapTransactionState(transactionState);

        // Update order status in your database
        updateOrderStatus(orderId, status);

        return status;
    }
    private void updateOrderStatus(String orderId, PaymentStatus status) {
        // Aquí iría la lógica para actualizar el estado del pedido en la base de datos.
        // Por ejemplo, usando un repositorio o un servicio adicional.
        try {
            // Simulación de lógica de actualización en base de datos
            logger.info("Actualizando el estado del pedido {} a {}", orderId, status);
            // Aquí puedes llamar a tu repositorio, como:
            // orderRepository.updateStatus(orderId, status);
        } catch (Exception e) {
            logger.error("Error actualizando el estado del pedido {}: {}", orderId, e.getMessage());
        }
    }



    private PaymentStatus mapTransactionState(String transactionState) {
        return switch (transactionState.toLowerCase()) {
            case "accepted" -> PaymentStatus.COMPLETED;
            case "rejected" -> PaymentStatus.REJECTED;
            case "pending" -> PaymentStatus.PENDING;
            default -> PaymentStatus.FAILED;
        };
    }

    private String generateSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
}
