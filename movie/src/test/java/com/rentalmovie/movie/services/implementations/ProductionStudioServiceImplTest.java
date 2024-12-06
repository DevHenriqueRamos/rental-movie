package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.repositories.ProductionStudioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionStudioServiceImplTest {

    @InjectMocks
    ProductionStudioServiceImpl productionStudioService;

    @Mock
    ProductionStudioRepository productionStudioRepository;

    @Test
    void findByIdTest_HappyPath() {
        when(productionStudioRepository.findById(any(UUID.class))).thenReturn(Optional.of(new ProductionStudioModel()));

        final var response = productionStudioService.findById(UUID.randomUUID());

        assertNotNull(response);
        assertEquals(ProductionStudioModel.class, response.getClass());

        verify(productionStudioRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findByIdTest_GenreNotFound() {
        when(productionStudioRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        UUID productionStudioId = UUID.randomUUID();
        try {
            productionStudioService.findById(productionStudioId);
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
            assertEquals("Production studio not found with id: " + productionStudioId, e.getMessage());
        }

        verify(productionStudioRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findAllTest_HappyPath() {
        Page<ProductionStudioModel> page = new PageImpl<>(List.of(new ProductionStudioModel(), new ProductionStudioModel()));
        Pageable pageable = PageRequest.of(0, 10);
        Specification<ProductionStudioModel> spec = ((root, query, criteriaBuilder) -> null);

        when(productionStudioRepository.findAll(spec, pageable)).thenReturn(page);

        final var response = productionStudioService.findAll(spec, pageable);
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(ProductionStudioModel.class, response.getContent().getFirst().getClass());

        verify(productionStudioRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void saveTest_HappyPath() {
        final var productionStudioModel = new ProductionStudioModel();
        productionStudioModel.setName("Test Genre");
        when(productionStudioRepository.save(any(ProductionStudioModel.class))).thenReturn(productionStudioModel);

        final var response = productionStudioService.save(productionStudioModel);
        assertNotNull(response);
        assertEquals(ProductionStudioModel.class, response.getClass());
        assertEquals(productionStudioModel.getProductionStudioId(), response.getProductionStudioId());
        assertEquals(productionStudioModel.getName(), response.getName());

        verify(productionStudioRepository, times(1)).save(any(ProductionStudioModel.class));
    }

    @Test
    void deleteTest_HappyPath() {
        doNothing().when(productionStudioRepository).delete(any(ProductionStudioModel.class));
        productionStudioService.delete(new ProductionStudioModel());
        verify(productionStudioRepository, times(1)).delete(any(ProductionStudioModel.class));
    }
}