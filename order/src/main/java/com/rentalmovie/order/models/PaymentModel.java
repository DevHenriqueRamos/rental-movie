package com.rentalmovie.order.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentModel {

    private UUID paymentId;
    private String paymentControl;
    private LocalDateTime paymentRequestDate;
    private LocalDateTime paymentCompletionDate;
    private BigDecimal valuePaid;
    private String paymentMessage;
}
