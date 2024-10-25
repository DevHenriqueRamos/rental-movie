package com.rentalmovie.movie.services;

import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.RentalPriceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalPriceMovieService {
    Page<RentalPriceModel> findAll(MovieModel movieModel, Pageable pageable);

    RentalPriceModel save(RentalPriceModel rentalPriceModel);
}
