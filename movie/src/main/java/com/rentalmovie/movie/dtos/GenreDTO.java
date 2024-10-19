package com.rentalmovie.movie.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
