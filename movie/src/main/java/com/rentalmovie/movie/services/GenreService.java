package com.rentalmovie.movie.services;

import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.specifications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface GenreService {
    Optional<GenreModel> findById(UUID genreId);

    GenreModel save(GenreModel genreModel);

    Page<GenreModel> findAll(SpecificationTemplate.GenreSpecification specification, Pageable pageable);

    void delete(GenreModel genreModel);
}
