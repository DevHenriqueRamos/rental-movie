package com.rentalmovie.notification.consumers;

import com.rentalmovie.notification.dtos.NotificationCommandDTO;
import com.rentalmovie.notification.enums.NotificationStatus;
import com.rentalmovie.notification.models.NotificationModel;
import com.rentalmovie.notification.services.NotificationService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class NotificationConsumer {

    @Autowired
    NotificationService notificationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.notificationCommandQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.notificationCommandExchange}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${rm.broker.key.notificationCommandKey}"
    ))
    public void listen(@Payload NotificationCommandDTO notificationCommandDTO) {
        var notificationModel = new NotificationModel();
        BeanUtils.copyProperties(notificationCommandDTO, notificationModel);
        notificationModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        notificationModel.setNotificationStatus(NotificationStatus.CREATED);
        notificationService.saveNotification(notificationModel);
    }
}
