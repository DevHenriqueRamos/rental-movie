package com.rentalmovie.rental.consumers;

import com.rentalmovie.rental.dtos.OrderEventDTO;
import com.rentalmovie.rental.dtos.RentalCommandDTO;
import com.rentalmovie.rental.enums.RentalStatus;
import com.rentalmovie.rental.models.RentalModel;
import com.rentalmovie.rental.publishers.RentalCommandPublisher;
import com.rentalmovie.rental.repositories.MovieRepository;
import com.rentalmovie.rental.repositories.RentalRepository;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class OrderConsumer {

    @Autowired
    RentalRepository rentalRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    RentalCommandPublisher rentalCommandPublisher;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${rm.broker.queue.orderEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${rm.broker.exchange.orderEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenOrderEvent(@Payload OrderEventDTO orderEventDTO) {
        LocalDateTime startRental = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime endRental = startRental.plusDays(7);

        for (UUID movieId : orderEventDTO.getMovieIds()) {
            var movieModel = movieRepository.findById(movieId).orElse(null);
            if (movieModel != null) {
                var rentalModel = new RentalModel();
                rentalModel.setRentalStatus(RentalStatus.ACTIVE);
                rentalModel.setRentalStart(startRental);
                rentalModel.setRentalEnd(endRental);
                rentalModel.setUserId(orderEventDTO.getUserId());
                rentalModel.setMovie(movieModel);
                rentalRepository.save(rentalModel);
            }
        }

        rentalCommandPublisher.publishRentalCommand(
                new RentalCommandDTO(orderEventDTO.getOrderId(), endRental)
        );
    }
}
