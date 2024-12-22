package com.rentalmovie.order.publishers;

import com.rentalmovie.order.dtos.OrderEventDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${rm.broker.exchange.orderEvent}")
    private String exchangeMovieEvent;

    public void publishOrderEvent(OrderEventDTO orderEventDTO) {
        log.info("Publishing order event {}", orderEventDTO.getOrderId());
        rabbitTemplate.convertAndSend(exchangeMovieEvent, "", orderEventDTO);
    }
}
