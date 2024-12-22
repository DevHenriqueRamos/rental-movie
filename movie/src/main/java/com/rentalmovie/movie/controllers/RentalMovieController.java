package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.clients.RentalClient;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.services.MovieService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class RentalMovieController {

    private final RentalClient rentalClient;
    private final MovieService movieService;

    public RentalMovieController(RentalClient rentalClient, MovieService movieService) {
        this.rentalClient = rentalClient;
        this.movieService = movieService;
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/movies/rentals")
    public ResponseEntity<Object> getAllRentalMovies(@RequestHeader("Authorization") String token) {

        List<UUID> movieIds = rentalClient.getAllActiveMoviesToUser(token);
        List<MovieModel> movies = movieService.findAllUserRentalMovies(movieIds);

        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }
}
