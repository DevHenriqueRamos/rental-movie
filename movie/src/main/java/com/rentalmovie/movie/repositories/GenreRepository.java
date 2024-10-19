package com.rentalmovie.movie.repositories;

import com.rentalmovie.movie.models.GenreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<GenreModel, UUID>, JpaSpecificationExecutor<GenreModel> {
    @Query(value = "select * from TB_GENRES where genre_id = :genreId and delete_status = 'ACTIVE'", nativeQuery = true)
    Optional<GenreModel> findActiveById(@Param("genreId") UUID genreId );
}
