package com.rentalmovie.movie.services;

import com.rentalmovie.movie.models.GenreModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface GenreService {
    GenreModel findById(UUID genreId);

    GenreModel save(GenreModel genreModel);

    Page<GenreModel> findAll(Specification<GenreModel> specification, Pageable pageable);

    void delete(GenreModel genreModel);
}
