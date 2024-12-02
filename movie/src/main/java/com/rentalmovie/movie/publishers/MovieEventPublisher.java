package com.rentalmovie.movie.publishers;

import com.rentalmovie.movie.dtos.MovieEventDTO;
import com.rentalmovie.movie.enums.ActionType;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MovieEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MovieEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${rm.broker.exchange.movieEvent}")
    private String exchangeMovieEvent;

    public void publishMovieEvent(MovieEventDTO movieEventDTO, ActionType actionType) {
        log.info("Publishing movie event {}", movieEventDTO);
        movieEventDTO.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeMovieEvent, "", movieEventDTO);
    }
}
