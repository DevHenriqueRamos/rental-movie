package com.rentalmovie.order.services;

import com.rentalmovie.order.dtos.PaymentEventDTO;
import com.rentalmovie.order.dtos.RentalCommandDTO;
import com.rentalmovie.order.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    OrderModel save(OrderModel orderModel);

    Page<OrderModel> findAllByUserId(UUID userId, Pageable pageable);

    OrderModel findByOrderIdAndUserId(UUID orderId, UUID userId);

    OrderModel processOrder(OrderModel orderModel, String paymentMethodId);

    void finishOrder(PaymentEventDTO paymentEventDTO);

    Optional<OrderModel> findLastUserOrder(UUID userId);

    void createNotification(RentalCommandDTO rentalCommandDTO);
}
