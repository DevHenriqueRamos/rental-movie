package com.rentalmovie.payment.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentEventDTO {

    private UUID paymentId;
    private String paymentControl;
    private LocalDateTime paymentRequestDate;
    private LocalDateTime paymentCompletionDate;
    private BigDecimal valuePaid;
    private String paymentMessage;
    private UUID userId;
    private UUID orderId;
}
