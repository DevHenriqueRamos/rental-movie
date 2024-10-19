package com.rentalmovie.movie.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductionStudioDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
