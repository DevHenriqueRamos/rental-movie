package com.rentalmovie.movie.services;

import com.rentalmovie.movie.models.ProductionStudioModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface ProductionStudioService {
    Optional<ProductionStudioModel> findById(UUID productionStudioId);

    ProductionStudioModel save(ProductionStudioModel productionStudioModel);

    Page<ProductionStudioModel> findAll(Specification<ProductionStudioModel> specification, Pageable pageable);

    void delete(ProductionStudioModel productionStudioModel);
}
