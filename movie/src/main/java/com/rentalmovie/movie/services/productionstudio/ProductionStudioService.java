package com.rentalmovie.movie.services.productionstudio;

import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.repositories.ProductionStudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductionStudioService implements IProductionStudioService {

    @Autowired
    private ProductionStudioRepository productionStudioRepository;

    @Override
    public ProductionStudioModel findById(UUID productionStudioId) {
        return productionStudioRepository.findById(productionStudioId).orElse(null);
    }

    @Override
    public ProductionStudioModel save(ProductionStudioModel productionStudioModel) {
        return productionStudioRepository.save(productionStudioModel);
    }

    @Override
    public Page<ProductionStudioModel> findAllActive(Specification<ProductionStudioModel> specification, Pageable pageable) {
        return productionStudioRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<ProductionStudioModel> findActiveById(UUID productionStudioId) {
        return productionStudioRepository.findActiveById(productionStudioId);
    }
}
