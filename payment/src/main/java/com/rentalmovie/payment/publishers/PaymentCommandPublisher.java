package com.rentalmovie.payment.publishers;

import com.rentalmovie.payment.dtos.PaymentCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentCommandPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${rm.broker.exchange.paymentCommandExchange}")
    private String paymentCommandExchange;

    @Value(value = "${rm.broker.key.paymentProcessCommandKey}")
    private String paymentCommandKey;

    public void publishPaymentProcessCommand(PaymentCommandDTO paymentCommandDTO) {
        rabbitTemplate.convertAndSend(paymentCommandExchange, paymentCommandKey, paymentCommandDTO);
    }

}
