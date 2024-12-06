package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.repositories.GenreRepository;
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
class GenreServiceImplTest {

    @InjectMocks
    GenreServiceImpl genreService;

    @Mock
    GenreRepository genreRepository;

    @Test
    void findByIdTest_HappyPath() {
        when(genreRepository.findById(any(UUID.class))).thenReturn(Optional.of(new GenreModel()));

        final var response = genreService.findById(UUID.randomUUID());

        assertNotNull(response);
        assertEquals(GenreModel.class, response.getClass());

        verify(genreRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findByIdTest_GenreNotFound() {
        when(genreRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        UUID genreId = UUID.randomUUID();
       try {
           genreService.findById(genreId);
       } catch (Exception e) {
           assertEquals(ResourceNotFoundException.class, e.getClass());
           assertEquals("Genre not found with id: " + genreId, e.getMessage());
       }

        verify(genreRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findAllTest_HappyPath() {
        Page<GenreModel> page = new PageImpl<>(List.of(new GenreModel(), new GenreModel()));
        Pageable pageable = PageRequest.of(0, 10);
        Specification<GenreModel> spec = ((root, query, criteriaBuilder) -> null);

        when(genreRepository.findAll(spec, pageable)).thenReturn(page);

        final var response = genreService.findAll(spec, pageable);
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(GenreModel.class, response.getContent().getFirst().getClass());

        verify(genreRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void saveTest_HappyPath() {
        final var genreModel = new GenreModel();
        genreModel.setName("Test Genre");
        when(genreRepository.save(any(GenreModel.class))).thenReturn(genreModel);

        final var response = genreService.save(genreModel);
        assertNotNull(response);
        assertEquals(GenreModel.class, response.getClass());
        assertEquals(genreModel.getGenreId(), response.getGenreId());
        assertEquals(genreModel.getName(), response.getName());

        verify(genreRepository, times(1)).save(any(GenreModel.class));
    }

    @Test
    void deleteTest_HappyPath() {
        doNothing().when(genreRepository).delete(any(GenreModel.class));
        genreService.delete(new GenreModel());
        verify(genreRepository, times(1)).delete(any(GenreModel.class));
    }

}