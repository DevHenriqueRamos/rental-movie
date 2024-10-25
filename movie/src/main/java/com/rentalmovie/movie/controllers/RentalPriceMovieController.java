package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.dtos.RentalPriceDTO;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.RentalPriceModel;
import com.rentalmovie.movie.services.MovieService;
import com.rentalmovie.movie.services.RentalPriceMovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
public class RentalPriceMovieController {

    @Autowired
    MovieService movieService;

    @Autowired
    RentalPriceMovieService rentalPriceMovieService;

    @GetMapping("/movies/{movieId}/rentalprice")
    public ResponseEntity<Object> getMovieRentalPrices(
            @PathVariable UUID movieId,
            @PageableDefault(page = 0, size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if (movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(rentalPriceMovieService.findAll(movieModelOptional.get(), pageable));
    }

    @PostMapping("/movies/{movieId}/rentalprice/create")
    public ResponseEntity<Object> createMovieRentalPrice(
            @PathVariable UUID movieId,
            @RequestBody @Valid RentalPriceDTO rentalPriceDTO
    ) {
        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if (movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        MovieModel movieModel = movieModelOptional.get();
        var rentalPriceModel = new RentalPriceModel();
        rentalPriceModel.setPrice(rentalPriceDTO.getPrice());
        rentalPriceModel.setMovie(movieModel);
        rentalPriceModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(rentalPriceMovieService.save(rentalPriceModel));
    }
}
