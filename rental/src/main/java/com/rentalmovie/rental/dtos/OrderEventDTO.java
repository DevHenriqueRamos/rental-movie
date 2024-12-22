package com.rentalmovie.rental.dtos;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class OrderEventDTO {

    private UUID orderId;
    private UUID userId;
    private Set<UUID> movieIds;

}
