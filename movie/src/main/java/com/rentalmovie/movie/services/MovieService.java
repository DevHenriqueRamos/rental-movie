package com.rentalmovie.movie.services;

import com.rentalmovie.movie.models.MovieModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MovieService {
    MovieModel save(MovieModel movieModel);

    Optional<MovieModel> findActiveById(UUID movieId);

    Page<MovieModel> findAllActive(Specification<MovieModel> specification, Pageable pageable);

    Object updateGenres(MovieModel movieModel, Set<UUID> genreIds);

    Object updateProductionStudio(MovieModel movieModel, UUID productionStudioId);
}
