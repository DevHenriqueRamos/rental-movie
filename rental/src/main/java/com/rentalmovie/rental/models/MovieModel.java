package com.rentalmovie.rental.models;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity(name = "TB_MOVIES")
public class MovieModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID movieId;

    @Column(nullable = false, length = 150)
    private String originalTitle;

    @Column(nullable = false, length = 150)
    private String translateTitle;
}
