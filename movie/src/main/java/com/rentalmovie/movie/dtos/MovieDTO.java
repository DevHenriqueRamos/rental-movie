package com.rentalmovie.movie.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieDTO {

    public interface MovieView {
        public static interface RegistrationPost {}
        public static interface MoviePut {}
        public static interface GenresPut {}

    }

    @NotBlank(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private String originalTitle;

    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private String translateTitle;

    @NotBlank(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private String synopsis;

    @NotNull(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private int releaseYear;

    @NotNull(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private int ageRange;

    @NotNull(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private int durationMinutes;

    @NotNull(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private double rating;

    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private String coverUrl;

    @NotBlank(groups = {MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    @JsonView({MovieView.RegistrationPost.class, MovieView.MoviePut.class})
    private String movieUrl;
    
    @JsonView({MovieView.GenresPut.class})
    private Set<UUID> genres;

    @NotNull(groups = {MovieView.RegistrationPost.class})
    @JsonView({MovieView.RegistrationPost.class})
    private BigDecimal price;
}
