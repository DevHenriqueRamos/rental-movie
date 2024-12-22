package com.rentalmovie.rental.publishers;

import com.rentalmovie.rental.dtos.RentalCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RentalCommandPublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${rm.broker.exchange.rentalCommandExchange}")
    private String rentalCommandExchange;

    @Value(value = "${rm.broker.key.rentalCommandKey}")
    private String rentalCommandKey;

    public void publishRentalCommand (RentalCommandDTO rentalCommandDTO) {
        rabbitTemplate.convertAndSend(rentalCommandExchange, rentalCommandKey, rentalCommandDTO);
    }
}
