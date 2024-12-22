package com.rentalmovie.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rentalmovie.payment.dtos.PaymentEventDTO;
import com.rentalmovie.payment.enums.PaymentControl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "TB_PAYMENTS")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentControl paymentControl;

    @Column(nullable = false)
    private LocalDateTime paymentRequestDate;

    @Column
    private LocalDateTime paymentCompletionDate;

    @Column(nullable = false)
    private BigDecimal valuePaid;

    @Column(length = 150)
    private String paymentMessage;

    public PaymentEventDTO convertToPaymentEventDto() {
        var paymentEventDTO = new PaymentEventDTO();
        BeanUtils.copyProperties(this, paymentEventDTO);
        paymentEventDTO.setPaymentControl(this.getPaymentControl().toString());
        return paymentEventDTO;
    }
}
