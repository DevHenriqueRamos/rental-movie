package com.rentalmovie.order.consumers;

import com.rentalmovie.order.dtos.MovieEventDTO;
import com.rentalmovie.order.enums.ActionType;
import com.rentalmovie.order.services.MovieService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MovieConsumer {

    @Autowired
    MovieService movieService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.movieEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.movieEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenMovieEvent(@Payload MovieEventDTO movieEventDTO) {
        var movieModel = movieEventDTO.convertToMovieModel();
        switch (ActionType.valueOf(movieEventDTO.getActionType())) {
            case CREATE:
            case UPDATE:
                movieService.save(movieModel);
                break;
            case DELETE:
                movieService.delete(movieModel);
                break;
        }
    }
}
