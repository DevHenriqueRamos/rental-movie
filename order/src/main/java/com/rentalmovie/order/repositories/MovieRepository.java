package com.rentalmovie.order.repositories;

import com.rentalmovie.order.models.MovieModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MovieRepository extends MongoRepository<MovieModel, UUID> {
}
