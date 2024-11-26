package com.misakguambshop.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id")
    private Long userId;

    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)  // Esto almacenará el nombre del enum como String en la base de datos
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "receipt_pdf_url")
    private String receiptPdfUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "redirect_url")
    private String redirectUrl;
}


