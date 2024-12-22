package com.rentalmovie.rental.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RentalCommandDTO {

    private UUID orderId;
    private LocalDateTime rentalDate;
}
