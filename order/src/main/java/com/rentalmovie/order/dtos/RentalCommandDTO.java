package com.rentalmovie.order.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RentalCommandDTO {

    private UUID orderId;
    private LocalDateTime rentalDate;
}
