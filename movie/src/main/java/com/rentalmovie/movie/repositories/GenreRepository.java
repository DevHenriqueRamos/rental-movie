package com.rentalmovie.movie.repositories;

import com.rentalmovie.movie.models.GenreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<GenreModel, UUID>, JpaSpecificationExecutor<GenreModel> {

}
