package com.rentalmovie.movie.repositories;

import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.RentalPriceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalPriceMovieRepository extends JpaRepository<RentalPriceModel, UUID> {
    Page<RentalPriceModel> findAllByMovie(MovieModel movieModel, Pageable pageable);
}
