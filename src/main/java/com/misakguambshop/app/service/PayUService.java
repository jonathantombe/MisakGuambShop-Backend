package com.misakguambshop.app.service;

import com.misakguambshop.app.config.PayUConfig;
import com.misakguambshop.app.controller.UserController;
import com.misakguambshop.app.dto.PaymentRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PayUService {
    @Autowired
    private PayUConfig payUConfig;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private Map<String, Object> createPaymentRequest(PaymentRequestDto requestDto, String signature) {
        Map<String, Object> request = new HashMap<>();

        // Merchant info
        Map<String, Object> merchant = new HashMap<>();
        merchant.put("apiKey", payUConfig.getApiKey());
        merchant.put("apiLogin", payUConfig.getApiLogin());
        request.put("merchant", merchant);

        // Transaction info
        Map<String, Object> transaction = new HashMap<>();

        // Order info
        Map<String, Object> order = new HashMap<>();
        order.put("accountId", payUConfig.getAccountId());
        order.put("referenceCode", requestDto.getReference());
        order.put("description", requestDto.getDescription());

        // TX_VALUE structure
        Map<String, Object> additionalValues = new HashMap<>();
        Map<String, Object> txValue = new HashMap<>();
        txValue.put("value", requestDto.getAmount().doubleValue());
        txValue.put("currency", requestDto.getCurrency());
        additionalValues.put("TX_VALUE", txValue);
        order.put("additionalValues", additionalValues);

        order.put("signature", signature);
        transaction.put("order", order);

        // Buyer info with shippingAddress
        Map<String, Object> buyer = new HashMap<>();
        buyer.put("merchantBuyerId", requestDto.getBuyerDocument());
        buyer.put("fullName", requestDto.getBuyerName());
        buyer.put("emailAddress", requestDto.getBuyerEmail());
        buyer.put("contactPhone", requestDto.getBuyerPhone());
        buyer.put("dniNumber", requestDto.getBuyerDocument());

        // Agregar dirección de envío
        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("street1", requestDto.getBillingAddress());
        shippingAddress.put("city", requestDto.getBillingCity());
        shippingAddress.put("state", requestDto.getBillingState());
        shippingAddress.put("country", requestDto.getBillingCountry());
        shippingAddress.put("postalCode", requestDto.getBillingPostalCode());
        buyer.put("shippingAddress", shippingAddress);

        transaction.put("buyer", buyer);

        // Payment method info
        transaction.put("type", "AUTHORIZATION_AND_CAPTURE");
        transaction.put("paymentMethod", convertToPayUPaymentMethod(requestDto.getPaymentMethod()));
        transaction.put("paymentCountry", "CO");

        // Credit card info
        if ("CREDIT_CARD".equals(requestDto.getPaymentMethod())) {
            Map<String, Object> creditCard = new HashMap<>();
            creditCard.put("number", requestDto.getCreditCardNumber());
            creditCard.put("securityCode", requestDto.getCreditCardSecurityCode());
            creditCard.put("expirationDate", requestDto.getCreditCardExpirationDate());
            creditCard.put("name", requestDto.getCreditCardName());
            transaction.put("creditCard", creditCard);
        }

        // Additional fields
        String deviceSessionId = UUID.randomUUID().toString();
        transaction.put("deviceSessionId", deviceSessionId);
        transaction.put("ipAddress", requestDto.getIpAddress());
        transaction.put("cookie", deviceSessionId);
        transaction.put("userAgent", "Mozilla/5.0");

        request.put("language", "es");
        request.put("command", "SUBMIT_TRANSACTION");
        request.put("test", Boolean.parseBoolean(payUConfig.getTest()));
        request.put("transaction", transaction);

        logger.debug("PayU Request: {}", request);
        return request;
    }

    public String processPayment(PaymentRequestDto requestDto) {
        try {
            String paymentUrl = payUConfig.getApiUrl();
            String signature = generateSignature(requestDto);

            Map<String, Object> request = createPaymentRequest(requestDto, signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            logger.debug("PayU Request URL: {}", paymentUrl);
            logger.debug("PayU Request Headers: {}", headers);
            logger.debug("PayU Request Body: {}", request);

            ResponseEntity<Map> response = restTemplate.postForEntity(paymentUrl, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("PayU returned non-OK status: {}", response.getStatusCode());
                throw new RuntimeException("Error from PayU: " + response.getStatusCode());
            }

            logger.debug("PayU Response: {}", response.getBody());

            if (response.getBody() != null) {
                Map<String, Object> responseMap = response.getBody();

                // Verificar si hay error en la respuesta
                if ("ERROR".equals(responseMap.get("code"))) {
                    String errorMessage = responseMap.containsKey("error") ?
                            responseMap.get("error").toString() : "Unknown error";
                    logger.error("PayU error: {}", errorMessage);
                    throw new RuntimeException("PayU error: " + errorMessage);
                }

                // Obtener la respuesta de la transacción
                Map<String, Object> transactionResponse =
                        (Map<String, Object>) responseMap.get("transactionResponse");

                if (transactionResponse != null) {
                    String transactionId = (String) transactionResponse.get("transactionId");
                    String state = (String) transactionResponse.get("state");

                    if (transactionId != null && "APPROVED".equals(state)) {
                        logger.info("Transaction successful. Transaction ID: {}", transactionId);
                        return transactionId;
                    } else {
                        String responseMessage = (String) transactionResponse.get("responseMessage");
                        logger.error("Transaction failed: {}", responseMessage);
                        throw new RuntimeException("Transaction failed: " + responseMessage);
                    }
                }
            }

            throw new RuntimeException("Invalid or unexpected response from PayU");
        } catch (Exception e) {
            logger.error("Error processing payment with PayU", e);
            throw new RuntimeException("Error processing payment with PayU: " + e.getMessage());
        }
    }

    private String convertToPayUPaymentMethod(String paymentMethod) {
        return switch (paymentMethod) {
            case "CREDIT_CARD" -> "MASTERCARD";
            case "PSE" -> "PSE";
            case "EFECTY" -> "EFECTY";
            case "NEQUI" -> "NEQUI";
            case "BANCOLOMBIA_TRANSFER" -> "BANCOLOMBIA_TRANSFER";
            case "DAVIPLATA" -> "DAVIPLATA";
            case "WOMPI" -> "WOMPI";
            case "BANCOLOMBIA_QR" -> "BANCOLOMBIA_QR";
            default -> throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        };
    }

    private String generateSignature(PaymentRequestDto requestDto) {
        try {
            String concatenated = String.format("%s~%s~%s~%.2f~%s",
                    payUConfig.getApiKey(),
                    payUConfig.getMerchantId(),
                    requestDto.getReference(),
                    requestDto.getAmount().setScale(2, RoundingMode.HALF_UP),
                    requestDto.getCurrency());

            logger.debug("Signature string before MD5: {}", concatenated);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(concatenated.getBytes(StandardCharsets.UTF_8));
            String signature = DatatypeConverter.printHexBinary(digest).toLowerCase();

            logger.debug("Generated signature: {}", signature);
            return signature;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    public void processRefund(String transactionId, BigDecimal refundAmount) {
        try {
            String refundUrl = payUConfig.getApiUrl() + "/refund";

            Map<String, Object> refundRequest = new HashMap<>();
            refundRequest.put("transactionId", transactionId);
            refundRequest.put("refundAmount", refundAmount);
            refundRequest.put("reason", "Customer request");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(refundRequest, headers);

            logger.debug("PayU Refund Request URL: {}", refundUrl);
            logger.debug("PayU Refund Request Body: {}", refundRequest);

            ResponseEntity<Map> response = restTemplate.postForEntity(refundUrl, entity, Map.class);
            logger.debug("PayU Refund Response: {}", response.getBody());

            if (response.getBody() != null && response.getBody().get("refundResponse") != null) {
                Map<String, Object> refundResponse = (Map<String, Object>) response.getBody().get("refundResponse");
                String refundId = (String) refundResponse.get("refundId");
                if (refundId != null) {
                    logger.info("Reembolso exitoso, ID de reembolso: {}", refundId);
                } else {
                    throw new RuntimeException("Reembolso fallido: No se recibió un ID de reembolso válido");
                }
            } else {
                logger.error("Respuesta no válida de PayU: {}", response.getBody());
                throw new RuntimeException("Respuesta no válida de PayU");
            }
        } catch (Exception e) {
            logger.error("Error procesando el reembolso con PayU", e);
            throw new RuntimeException("Error procesando el reembolso con PayU: " + e.getMessage());
        }
    }
}


