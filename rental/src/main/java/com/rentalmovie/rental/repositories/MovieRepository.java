package com.rentalmovie.rental.repositories;

import com.rentalmovie.rental.models.MovieModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieRepository extends JpaRepository<MovieModel, UUID> {
}
