package com.rentalmovie.order.consumers;

import com.rentalmovie.order.dtos.PaymentEventDTO;
import com.rentalmovie.order.services.OrderService;
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
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.paymentEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.paymentEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenPaymentEvent(@Payload PaymentEventDTO paymentEventDTO) {
        orderService.finishOrder(paymentEventDTO);
    }
}
