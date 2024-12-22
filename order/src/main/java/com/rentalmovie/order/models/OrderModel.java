package com.rentalmovie.order.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rentalmovie.order.enums.OrderStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "orders")
public class OrderModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID orderId;
    private UUID userId;
    private Set<MovieModel> movies;
    private PaymentModel payment;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private LocalDateTime rentalExpirationDate;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public OrderModel () {
        this.orderId = UUID.randomUUID();
    }
}
