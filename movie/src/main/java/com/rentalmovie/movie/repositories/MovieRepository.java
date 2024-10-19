package com.rentalmovie.movie.repositories;

import com.rentalmovie.movie.models.MovieModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<MovieModel, UUID>, JpaSpecificationExecutor<MovieModel> {

    @Query(value = "select * from tb_movies where movie_id = :movieId and delete_status = 'ACTIVE'", nativeQuery = true)
    Optional<MovieModel> findActiveById(@Param(value = "movieId") UUID movieId);
}
