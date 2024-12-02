package com.rentalmovie.order.dtos;

import com.rentalmovie.order.models.MovieModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MovieEventDTO {

    private UUID movieId;
    private String originalTitle;
    private String translateTitle;
    private BigDecimal price;
    private String actionType;

    public MovieModel convertToMovieModel() {
        var movieModel = new MovieModel();
        BeanUtils.copyProperties(this, movieModel);
        return movieModel;
    }
}
