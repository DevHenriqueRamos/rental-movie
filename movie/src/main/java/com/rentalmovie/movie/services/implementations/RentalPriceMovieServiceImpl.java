package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.RentalPriceModel;
import com.rentalmovie.movie.repositories.RentalPriceMovieRepository;
import com.rentalmovie.movie.services.RentalPriceMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RentalPriceMovieServiceImpl implements RentalPriceMovieService {

    @Autowired
    RentalPriceMovieRepository rentalPriceMovieRepository;

    @Override
    public Page<RentalPriceModel> findAll(MovieModel movieModel, Pageable pageable) {
        return rentalPriceMovieRepository.findAllByMovie(movieModel, pageable);
    }

    @Override
    public RentalPriceModel save(RentalPriceModel rentalPriceModel) {
        return rentalPriceMovieRepository.save(rentalPriceModel);
    }
}
