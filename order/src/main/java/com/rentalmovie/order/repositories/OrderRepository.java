package com.rentalmovie.order.repositories;

import com.rentalmovie.order.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends MongoRepository<OrderModel, UUID> {
    Page<OrderModel> findAllByUserId(UUID userId, Pageable pageable);
    Optional<OrderModel> findByOrderIdAndUserId(UUID orderId, UUID userId);
}
