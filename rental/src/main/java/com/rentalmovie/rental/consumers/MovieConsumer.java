package com.rentalmovie.rental.consumers;

import com.rentalmovie.rental.dtos.MovieEventDTO;
import com.rentalmovie.rental.enums.ActionType;
import com.rentalmovie.rental.models.MovieModel;
import com.rentalmovie.rental.repositories.MovieRepository;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MovieConsumer {

    @Autowired
    MovieRepository movieRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.movieEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.movieEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenMovieEvent(@Payload MovieEventDTO movieEventDTO) {
        var movieModel = new MovieModel();
        BeanUtils.copyProperties(movieEventDTO, movieModel);

        switch (ActionType.valueOf(movieEventDTO.getActionType())) {
            case CREATE:
            case UPDATE:
                movieRepository.save(movieModel);
                break;
            case DELETE:
                deleteMovie(movieModel);
                break;
        }
    }

    private void deleteMovie(MovieModel movieModel) {
        if (existsMovie(movieModel.getMovieId())) {
            movieRepository.delete(movieModel);
        }
    }

    private boolean existsMovie(UUID movieId) {
        Optional<MovieModel> movieModel = movieRepository.findById(movieId);

        return movieModel.isPresent();
    }
}
