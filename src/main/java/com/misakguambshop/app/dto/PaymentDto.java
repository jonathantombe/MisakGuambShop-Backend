package com.misakguambshop.app.dto;

import com.misakguambshop.app.model.PaymentMethod;
import com.misakguambshop.app.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Integer orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String paymentReference;
    private LocalDateTime paymentDate;
    private String receiptPdfUrl;
    private LocalDateTime updatedAt;
    private PaymentStatus status;
    private String paymentUrl;
    private String redirectUrl;
}
