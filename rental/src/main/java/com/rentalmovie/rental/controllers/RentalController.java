package com.rentalmovie.rental.controllers;

import com.rentalmovie.rental.configs.security.AuthenticationCurrentUserService;
import com.rentalmovie.rental.services.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/rental")
public class RentalController {

    private final RentalService rentalService;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public RentalController(RentalService rentalService, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.rentalService = rentalService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping
    public ResponseEntity<Object> getAllActiveMovies() {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();

        return ResponseEntity.status(HttpStatus.OK).body(rentalService.findAllActiveMoviesByUserId(userId));
    }
}
