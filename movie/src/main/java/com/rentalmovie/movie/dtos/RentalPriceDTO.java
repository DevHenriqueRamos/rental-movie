package com.rentalmovie.movie.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RentalPriceDTO {

    @NotNull
    private BigDecimal price;
}
