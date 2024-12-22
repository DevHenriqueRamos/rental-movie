package com.rentalmovie.rental.repositories;

import com.rentalmovie.rental.enums.RentalStatus;
import com.rentalmovie.rental.models.RentalModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<RentalModel, UUID> {

    List<RentalModel> findAllByUserIdAndRentalStatus(UUID userId, RentalStatus rentalStatus);

    List<RentalModel> findAllByRentalEndAfterAndRentalStatus(LocalDateTime rentalEndAfter, RentalStatus rentalStatus);

    List<RentalModel> findAllByRentalStartBetweenAndRentalStatus(LocalDateTime rentalStart, LocalDateTime rentalEnd, RentalStatus rentalStatus);
}
