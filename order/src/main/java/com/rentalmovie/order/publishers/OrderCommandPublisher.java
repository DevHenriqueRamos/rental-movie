package com.rentalmovie.order.publishers;

import com.rentalmovie.order.dtos.PaymentRequestDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderCommandPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${rm.broker.exchange.paymentCommandExchange}")
    private String paymentCommandExchange;

    @Value(value = "${rm.broker.key.paymentRequestCommandKey}")
    private String paymentCommandKey;

    public void publishPaymentRequestCommand (PaymentRequestDTO paymentRequestDTO) {
        rabbitTemplate.convertAndSend(paymentCommandExchange, paymentCommandKey, paymentRequestDTO);
    }
}
