package com.rentalmovie.rental.services.impl;

import com.rentalmovie.rental.dtos.NotificationCommandDTO;
import com.rentalmovie.rental.enums.RentalStatus;
import com.rentalmovie.rental.models.MovieModel;
import com.rentalmovie.rental.models.RentalModel;
import com.rentalmovie.rental.publishers.NotificationCommandPublisher;
import com.rentalmovie.rental.repositories.RentalRepository;
import com.rentalmovie.rental.services.RentalService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final NotificationCommandPublisher notificationCommandPublisher;

    public RentalServiceImpl(RentalRepository rentalRepository, NotificationCommandPublisher notificationCommandPublisher) {
        this.rentalRepository = rentalRepository;
        this.notificationCommandPublisher = notificationCommandPublisher;
    }

    @Override
    public List<UUID> findAllActiveMoviesByUserId(UUID userId) {
        List<RentalModel> listRentalModel = rentalRepository.findAllByUserIdAndRentalStatus(userId, RentalStatus.ACTIVE);

        return listRentalModel.stream().map(
                rentalModel -> rentalModel.getMovie().getMovieId()
        ).collect(Collectors.toList());
    }

    @Override
    public void expireRentals() {
        List<RentalModel> listRentalModel = rentalRepository.findAllByRentalEndAfterAndRentalStatus(
                LocalDateTime.now(ZoneId.of("UTC")),
                RentalStatus.ACTIVE
        );

        Map<UUID, Set<MovieModel>> userMovies = new HashMap<>();

        for (RentalModel rentalModel : listRentalModel) {
            rentalModel.setRentalStatus(RentalStatus.EXPIRED);
            rentalRepository.save(rentalModel);

            userMovies.computeIfAbsent(rentalModel.getUserId(), key -> new HashSet<>())
                    .add(rentalModel.getMovie());
        }

        for (Map.Entry<UUID, Set<MovieModel>> entry : userMovies.entrySet()) {
            var notification = new NotificationCommandDTO();
            notification.setUserId(entry.getKey());
            notification.setTitle("Bem amigos, terminou!");
            notification.setMessage("O aluguel dos dos seguintes filmes expirou: " + movieNames(entry.getValue()));
            notificationCommandPublisher.publishNotificationCommand(notification);
        }
    }

    @Override
    public void nearExpireRentals() {
        List<RentalModel> listRentalModel = rentalRepository.findAllByRentalStartBetweenAndRentalStatus(
                LocalDateTime.now(ZoneId.of("UTC")),
                LocalDateTime.now(ZoneId.of("UTC")).plusDays(1).minusNanos(1),
                RentalStatus.ACTIVE
        );

        Map<UUID, Set<MovieModel>> userMovies = new HashMap<>();

        for (RentalModel rentalModel : listRentalModel) {
            userMovies.computeIfAbsent(rentalModel.getUserId(), key -> new HashSet<>())
                    .add(rentalModel.getMovie());
        }

        for (Map.Entry<UUID, Set<MovieModel>> entry : userMovies.entrySet()) {
            var notification = new NotificationCommandDTO();
            notification.setUserId(entry.getKey());
            notification.setTitle("Próximos do fim!");
            notification.setMessage("Os filmes estão próximos de expirar: " + movieNames(entry.getValue()));
            notificationCommandPublisher.publishNotificationCommand(notification);
        }
    }

    private String movieNames(Set<MovieModel> movieModels) {
        StringBuilder movieTitles = new StringBuilder();

        Iterator<MovieModel> iterator = movieModels.iterator();
        while (iterator.hasNext()) {
            MovieModel movieModel = iterator.next();
            movieTitles.append(movieModel.getTranslateTitle());
            if (iterator.hasNext()) {
                movieTitles.append(", ");
            }
        }

        return movieTitles.toString();
    }
}
