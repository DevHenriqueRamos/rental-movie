package com.rentalmovie.rental.publishers;

import com.rentalmovie.rental.dtos.NotificationCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${rm.broker.exchange.notificationCommandExchange}")
    private String notificationCommandExchange;

    @Value(value = "${rm.broker.key.notificationCommandKey}")
    private String notificationCommandKey;

    public void publishNotificationCommand (NotificationCommandDTO notificationCommandDTO) {
        rabbitTemplate.convertAndSend(notificationCommandExchange, notificationCommandKey, notificationCommandDTO);
    }

}
