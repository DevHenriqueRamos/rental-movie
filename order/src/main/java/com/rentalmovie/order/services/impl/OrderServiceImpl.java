package com.rentalmovie.order.services.impl;

import com.rentalmovie.order.dtos.*;
import com.rentalmovie.order.enums.OrderStatus;
import com.rentalmovie.order.enums.PaymentControl;
import com.rentalmovie.order.exceptions.ResourceNotFoundException;
import com.rentalmovie.order.models.MovieModel;
import com.rentalmovie.order.models.OrderModel;
import com.rentalmovie.order.models.PaymentModel;
import com.rentalmovie.order.publishers.NotificationCommandPublisher;
import com.rentalmovie.order.publishers.OrderCommandPublisher;
import com.rentalmovie.order.publishers.OrderEventPublisher;
import com.rentalmovie.order.repositories.OrderRepository;
import com.rentalmovie.order.services.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderCommandPublisher orderCommandPublisher;
    private final OrderEventPublisher orderEventPublisher;
    private final NotificationCommandPublisher notificationCommandPublisher;

    public OrderServiceImpl(OrderRepository orderRepository, OrderCommandPublisher orderCommandPublisher, OrderEventPublisher orderEventPublisher, NotificationCommandPublisher notificationCommandPublisher) {
        this.orderRepository = orderRepository;
        this.orderCommandPublisher = orderCommandPublisher;
        this.orderEventPublisher = orderEventPublisher;
        this.notificationCommandPublisher = notificationCommandPublisher;
    }

    @Override
    public OrderModel save(OrderModel orderModel) {
        return orderRepository.save(orderModel);
    }

    @Override
    public Page<OrderModel> findAllByUserId(UUID userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public OrderModel findByOrderIdAndUserId(UUID orderId, UUID userId) {
        return orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found to this user"));
    }

    @Override
    public Optional<OrderModel> findLastUserOrder(UUID userId) {
        return orderRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void createNotification(RentalCommandDTO rentalCommandDTO) {
        var orderModel = orderRepository.findById(rentalCommandDTO.getOrderId());
        NotificationCommandDTO notificationCommandDTO = new NotificationCommandDTO();

        if (orderModel.isPresent()) {
            notificationCommandDTO.setUserId(orderModel.get().getUserId());
            notificationCommandDTO.setTitle("Transação realizada com sucesso!");
            notificationCommandDTO.setMessage(notificationMessage(orderModel.get()));

            notificationCommandPublisher.publishNotificationCommand(notificationCommandDTO);
        }
    }

    private String notificationMessage(OrderModel orderModel) {
        StringBuilder movieTitles = new StringBuilder();

        Iterator<MovieModel> iterator = orderModel.getMovies().iterator();
        while (iterator.hasNext()) {
            MovieModel movieModel = iterator.next();
            movieTitles.append(movieModel.getTranslateTitle());
            if (iterator.hasNext()) {
                movieTitles.append(", ");
            }
        }

        LocalDate expirationDate = orderModel.getRentalExpirationDate().atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toLocalDate();

        String formatDate = expirationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return "O aluguel dos filmes: %s foi concluída com sucesso, aproveite até %s".formatted(movieTitles.toString(), formatDate);
    }

    @Override
    public OrderModel processOrder(OrderModel orderModel, String paymentMethodId) {
        orderModel = orderRepository.save(orderModel);
        try {
            PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
            paymentRequestDTO.setOrderId(orderModel.getOrderId());
            paymentRequestDTO.setUserId(orderModel.getUserId());
            paymentRequestDTO.setTotalAmount(orderModel.getTotalPrice());
            paymentRequestDTO.setPaymentMethodId(paymentMethodId);

            orderCommandPublisher.publishPaymentRequestCommand(paymentRequestDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return orderModel;
    }

    @Override
    public void finishOrder(PaymentEventDTO paymentEventDTO) {
        var orderModel = findByOrderIdAndUserId(paymentEventDTO.getOrderId(), paymentEventDTO.getUserId());

        if (paymentEventDTO.getPaymentControl().equals(PaymentControl.EFFECTED.name())) {
            PaymentModel paymentModel = new PaymentModel();
            BeanUtils.copyProperties(paymentEventDTO, paymentModel);

            orderModel.setStatus(OrderStatus.COMPLETED);
            orderModel.setOrderDate(paymentEventDTO.getPaymentCompletionDate());
            orderModel.setRentalExpirationDate(paymentEventDTO.getPaymentCompletionDate().plusDays(7));
            orderModel.setPayment(paymentModel);
            orderRepository.save(orderModel);

            orderEventPublisher.publishOrderEvent(createOrderEvent(orderModel));
        } else {
            PaymentModel paymentModel = new PaymentModel();
            BeanUtils.copyProperties(paymentEventDTO, paymentModel);

            orderModel.setStatus(OrderStatus.CANCELLED);
            orderModel.setOrderDate(paymentEventDTO.getPaymentCompletionDate());
            orderModel.setRentalExpirationDate(paymentEventDTO.getPaymentCompletionDate());
            orderModel.setPayment(paymentModel);
            orderRepository.save(orderModel);
        }
    }

    private OrderEventDTO createOrderEvent(OrderModel orderModel) {
        Set<UUID> movieIds = orderModel.getMovies().stream().map(MovieModel::getMovieId).collect(Collectors.toSet());

        return OrderEventDTO.builder()
                .orderId(orderModel.getOrderId())
                .userId(orderModel.getUserId())
                .movieIds(movieIds)
                .build();
    }
}
