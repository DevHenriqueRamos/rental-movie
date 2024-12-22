package com.rentalmovie.rental.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MovieEventDTO {

    private UUID movieId;
    private String originalTitle;
    private String translateTitle;
    private BigDecimal price;
    private String actionType;
}
