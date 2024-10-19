package com.rentalmovie.movie.repositories;

import com.rentalmovie.movie.models.ProductionStudioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductionStudioRepository extends JpaRepository<ProductionStudioModel, UUID>, JpaSpecificationExecutor<ProductionStudioModel> {
    @Query(value = "select * from TB_PRODUCTION_STUDIOS where production_studio_id = :productionStudioId and delete_status = 'ACTIVE'", nativeQuery = true)
    Optional<ProductionStudioModel> findActiveById(@Param("productionStudioId") UUID productionStudioId);
}
