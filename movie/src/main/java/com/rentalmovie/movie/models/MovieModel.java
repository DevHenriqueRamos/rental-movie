package com.rentalmovie.movie.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rentalmovie.movie.dtos.MovieEventDTO;
import com.rentalmovie.movie.enums.DeleteStatus;
import jakarta.persistence.*;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "TB_MOVIES")
public class MovieModel extends RepresentationModel<MovieModel> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID movieId;

    @Column(nullable = false, length = 150)
    private String originalTitle;

    @Column(length = 150)
    private String translateTitle;

    @Column(nullable = false, length = 250)
    private String synopsis;

    @Column(nullable = false)
    private int releaseYear;

    @Column(nullable = false)
    private int ageRange;

    @Column(nullable = false)
    private int durationMinutes;

    @Column
    private double rating;

    @Column
    private String coverUrl;

    @Column(nullable = false)
    private String movieUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeleteStatus deleteStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'z'")
    @Column(nullable = false)
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'z'")
    @Column(nullable = false)
    private LocalDateTime lastUpdateDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "TB_GENRE_MOVIE",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")}
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<GenreModel> genres;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductionStudioModel productionStudio;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RentalPriceModel> rentalPrices = new HashSet<>();

    public MovieEventDTO convertToMovieEventDTO() {
        var movieEventDTO = new MovieEventDTO();
        BeanUtils.copyProperties(this, movieEventDTO);
        RentalPriceModel rentalPriceModel = this.rentalPrices.stream().max(Comparator.comparing(RentalPriceModel::getCreationDate))
                .orElseThrow(() -> new NotFoundException("Anyone price to this movie found."));
        movieEventDTO.setPrice(rentalPriceModel.getPrice());
        return movieEventDTO;
    }

    public void addRentalPrice(BigDecimal price) {
        var rentalPriceModel = new RentalPriceModel();
        rentalPriceModel.setPrice(price);
        rentalPriceModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        this.rentalPrices.add(rentalPriceModel);
    }
}
