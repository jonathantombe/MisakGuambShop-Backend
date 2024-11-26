package com.misakguambshop.app.controller;

import com.misakguambshop.app.dto.PaymentDto;
import com.misakguambshop.app.model.PaymentStatus;
import com.misakguambshop.app.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import java.util.Map;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/success")
    public ResponseEntity<Object> handlePaymentResponse(
            @RequestParam("ref_payco") String referenceCode,
            @RequestParam("x_transaction_state") String transactionState) {

        logger.info("Payment response received - Reference: {}, State: {}",
                referenceCode, transactionState);

        try {
            PaymentStatus status = paymentService.getPaymentStatus(referenceCode);
            // Redirect to frontend with status
            String redirectUrl = String.format(
                    "http://your-frontend-url/payment/status?reference=%s&status=%s",
                    referenceCode,
                    status.toString()
            );
            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            logger.error("Error processing payment response", e);
            return ResponseEntity.status(500).body("Error processing payment");
        }
    }

    @PostMapping("/confirmation")
    public ResponseEntity<String> handlePaymentConfirmation(@RequestBody Map<String, Object> payload) {
        logger.info("Received ePayco confirmation: {}", payload);

        try {
            // Validate the signature
            if (!paymentService.validateSignature(payload)) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            String referenceCode = (String) payload.get("x_ref_payco");
            String transactionState = (String) payload.get("x_transaction_state");
            String orderId = (String) payload.get("x_extra1");

            PaymentStatus status = paymentService.processPaymentConfirmation(
                    referenceCode,
                    transactionState,
                    orderId
            );

            return ResponseEntity.ok("Confirmation processed successfully");
        } catch (Exception e) {
            logger.error("Error processing payment confirmation", e);
            return ResponseEntity.status(500).body("Error processing confirmation");
        }
    }

}


