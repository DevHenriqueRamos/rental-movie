package com.rentalmovie.movie.services.productionstudio;

import com.rentalmovie.movie.models.ProductionStudioModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface IProductionStudioService {
    ProductionStudioModel findById(UUID productionStudioId);

    ProductionStudioModel save(ProductionStudioModel productionStudioModel);

    Page<ProductionStudioModel> findAllActive(Specification<ProductionStudioModel> specification, Pageable pageable);

    Optional<ProductionStudioModel> findActiveById(UUID productionStudioId);
}
