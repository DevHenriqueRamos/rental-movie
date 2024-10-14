package com.rentalmovie.movie.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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
    private String ageRange;

    @Column(nullable = false)
    private int durationMinutes;

    @Column
    private double rating;

    @Column
    private String coverUrl;

    @Column(nullable = false)
    private String movieUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime lastUpdateDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "movies")
    private Set<GenreModel> genres;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(optional = false)
    private ProductionStudioModel productionStudio;

}
