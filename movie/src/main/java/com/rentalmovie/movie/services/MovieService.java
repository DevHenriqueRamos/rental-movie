package com.rentalmovie.movie.services;

import com.rentalmovie.movie.models.MovieModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public interface MovieService {
    MovieModel save(MovieModel movieModel);

    MovieModel findActiveById(UUID movieId);

    Page<MovieModel> findAllActive(Specification<MovieModel> specification, Pageable pageable);

    MovieModel updateGenres(MovieModel movieModel, Set<UUID> genreIds);

    MovieModel updateProductionStudio(MovieModel movieModel, UUID productionStudioId);

    MovieModel saveMovie(MovieModel movieModel);

    MovieModel updateMovie(MovieModel movieModel);

    void deleteMovie(MovieModel movieModel);
}
