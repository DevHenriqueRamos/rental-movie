package com.rentalmovie.order.models;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Document(collection = "movies")
public class MovieModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private UUID movieId;
    private String originalTitle;
    private String translateTitle;
    private BigDecimal price;
}
