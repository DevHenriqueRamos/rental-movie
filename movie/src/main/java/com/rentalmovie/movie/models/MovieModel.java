package com.rentalmovie.movie.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rentalmovie.movie.enums.DeleteStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "TB_MOVIES")
public class MovieModel implements Serializable {
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

}
