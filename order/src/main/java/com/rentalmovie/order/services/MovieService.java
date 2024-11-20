package com.rentalmovie.order.services;

import com.rentalmovie.order.models.MovieModel;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MovieService {
    Optional<MovieModel> findById(UUID movieId);

    Set<MovieModel> getMovieSet(@NotNull Set<UUID> moviesIds);
}
