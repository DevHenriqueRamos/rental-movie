package com.rentalmovie.payment.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentCommandDTO {

    private UUID orderId;
    private UUID paymentId;
    private String paymentMethodId;
}
