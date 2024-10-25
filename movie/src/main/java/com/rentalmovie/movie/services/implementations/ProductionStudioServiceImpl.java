package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.repositories.ProductionStudioRepository;
import com.rentalmovie.movie.services.ProductionStudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductionStudioServiceImpl implements ProductionStudioService {

    @Autowired
    ProductionStudioRepository productionStudioRepository;

    @Override
    public Optional<ProductionStudioModel> findById(UUID productionStudioId) {
        return productionStudioRepository.findById(productionStudioId);
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
