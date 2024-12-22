package com.rentalmovie.payment.consumers;

import com.rentalmovie.payment.dtos.PaymentCommandDTO;
import com.rentalmovie.payment.dtos.PaymentRequestDTO;
import com.rentalmovie.payment.services.PaymentService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.paymentRequestCommandQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.paymentCommandExchange}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${rm.broker.key.paymentRequestCommandKey}"
    ))
    public void listenPaymentRequestCommand(@Payload PaymentRequestDTO paymentRequestDTO) {
        paymentService.requestPayment(paymentRequestDTO);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.paymentProcessCommandQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.paymentCommandExchange}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${rm.broker.key.paymentProcessCommandKey}"
    ))
    public void listenPaymentProcessCommand(@Payload PaymentCommandDTO paymentCommandDTO) {
        paymentService.makePayment(paymentCommandDTO);
    }
}
