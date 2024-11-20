package com.rentalmovie.order.services;

import com.rentalmovie.order.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    OrderModel save(OrderModel orderModel);

    Page<OrderModel> findAllByUserId(UUID userId, Pageable pageable);

    Optional<OrderModel> findByOrderIdAndUserId(UUID orderId, UUID userId);
}
