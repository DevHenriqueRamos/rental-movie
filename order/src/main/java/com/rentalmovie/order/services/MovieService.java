package com.rentalmovie.order.services;

import com.rentalmovie.order.models.MovieModel;

import java.util.Set;
import java.util.UUID;

public interface MovieService {
    MovieModel findById(UUID movieId);

    Set<MovieModel> getMovieSet( Set<UUID> moviesIds);

    MovieModel save( MovieModel movieModel);

    void delete(MovieModel movieModel);
}
