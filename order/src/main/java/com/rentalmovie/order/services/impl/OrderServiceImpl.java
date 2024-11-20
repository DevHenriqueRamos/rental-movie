package com.rentalmovie.order.services.impl;

import com.rentalmovie.order.models.OrderModel;
import com.rentalmovie.order.repositories.OrderRepository;
import com.rentalmovie.order.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Override
    public OrderModel save(OrderModel orderModel) {
        return orderRepository.save(orderModel);
    }

    @Override
    public Page<OrderModel> findAllByUserId(UUID userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Optional<OrderModel> findByOrderIdAndUserId(UUID orderId, UUID userId) {
        return orderRepository.findByOrderIdAndUserId(orderId, userId);
    }
}
