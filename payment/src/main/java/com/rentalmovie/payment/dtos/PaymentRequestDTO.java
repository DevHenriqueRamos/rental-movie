package com.rentalmovie.payment.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDTO {

    @NotBlank
    private UUID orderId;

    @NotBlank
    private UUID userId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=10, fraction=2)
    private BigDecimal totalAmount;

    @NotBlank
    private String paymentMethodId;
}
