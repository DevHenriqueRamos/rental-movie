package com.rentalmovie.movie.services.genre;

import com.rentalmovie.movie.models.GenreModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface IGenreService {
    Optional<GenreModel> findById(UUID genreId);

    GenreModel save(GenreModel genreModel);

    Page<GenreModel> findAllActive(Specification<GenreModel> specification, Pageable pageable);

    Optional<GenreModel> findActiveById(UUID genreId);
}
