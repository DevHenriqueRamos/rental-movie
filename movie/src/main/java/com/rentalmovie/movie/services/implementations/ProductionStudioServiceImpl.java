package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.repositories.ProductionStudioRepository;
import com.rentalmovie.movie.services.ProductionStudioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductionStudioServiceImpl implements ProductionStudioService {

    private final ProductionStudioRepository productionStudioRepository;

    public ProductionStudioServiceImpl(ProductionStudioRepository productionStudioRepository) {
        this.productionStudioRepository = productionStudioRepository;
    }

    @Override
    public ProductionStudioModel findById(UUID productionStudioId) {
        return productionStudioRepository.findById(productionStudioId)
                .orElseThrow(() -> new ResourceNotFoundException("Production studio not found with id: " + productionStudioId));
    }

    @Override
    public ProductionStudioModel save(ProductionStudioModel productionStudioModel) {
        return productionStudioRepository.save(productionStudioModel);
    }

    @Override
    public Page<ProductionStudioModel> findAll(Specification<ProductionStudioModel> specification, Pageable pageable) {
        return productionStudioRepository.findAll(specification, pageable);
    }

    @Override
    public void delete(ProductionStudioModel productionStudioModel) {
        productionStudioRepository.delete(productionStudioModel);
    }
}
