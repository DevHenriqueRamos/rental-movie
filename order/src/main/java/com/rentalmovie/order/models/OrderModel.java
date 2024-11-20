package com.rentalmovie.order.models;

import com.rentalmovie.order.enums.OrderStatus;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Document(collection = "orders")
public class OrderModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final UUID orderId = UUID.randomUUID();
    private UUID userId;
    private Set<MovieModel> movies;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private LocalDateTime rentalExpirationDate;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
