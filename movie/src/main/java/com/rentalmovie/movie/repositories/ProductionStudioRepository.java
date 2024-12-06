package com.rentalmovie.movie.repositories;

import com.rentalmovie.movie.models.ProductionStudioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductionStudioRepository extends JpaRepository<ProductionStudioModel, UUID>, JpaSpecificationExecutor<ProductionStudioModel> {
    Optional<ProductionStudioModel> findByName(String name);
}
