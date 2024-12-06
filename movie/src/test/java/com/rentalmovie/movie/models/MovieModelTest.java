package com.rentalmovie.movie.models;

import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MovieModelTest {

    @Test
    void convertToMovieEventDTOTest_HappyPath() {
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());
        movieModel.setOriginalTitle("Movie title Test");
        final var rentalPriceModel = new RentalPriceModel(UUID.randomUUID(), BigDecimal.valueOf(15.00), LocalDateTime.now());
        movieModel.getRentalPrices().add(rentalPriceModel);

        final var result = movieModel.convertToMovieEventDTO();
        assertEquals(movieModel.getMovieId(), result.getMovieId());
        assertEquals(movieModel.getOriginalTitle(), result.getOriginalTitle());
        assertEquals(rentalPriceModel.getPrice(), result.getPrice());
    }

    @Test
    void convertToMovieEventDTOTest_NotFoundPriceToMovie() {
        final var movieModel = new MovieModel();
        movieModel.getRentalPrices().clear();

        try {
            movieModel.convertToMovieEventDTO();
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
            assertEquals("Anyone price to this movie found.", e.getMessage());
        }
    }

    @Test
    void addRentalPrice_HappyPath() {
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());
        movieModel.addRentalPrice(BigDecimal.valueOf(15.00));

        RentalPriceModel rentalPriceModel = movieModel.getRentalPrices().stream().findFirst().orElse(null);
        assertNotNull(rentalPriceModel);
        assertEquals(1, movieModel.getRentalPrices().size());
        assertEquals(BigDecimal.valueOf(15.00), rentalPriceModel.getPrice());
    }
}