package com.rentalmovie.rental.services;

import java.util.List;
import java.util.UUID;

public interface RentalService {

    List<UUID> findAllActiveMoviesByUserId(UUID userId);

    void expireRentals();

    void nearExpireRentals();
}
