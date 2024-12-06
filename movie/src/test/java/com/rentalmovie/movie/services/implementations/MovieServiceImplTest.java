package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.enums.ActionType;
import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.models.RentalPriceModel;
import com.rentalmovie.movie.publishers.MovieEventPublisher;
import com.rentalmovie.movie.repositories.MovieRepository;
import com.rentalmovie.movie.services.GenreService;
import com.rentalmovie.movie.services.ProductionStudioService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @InjectMocks
    MovieServiceImpl movieService;

    @Mock
    MovieRepository movieRepository;

    @Mock
    GenreService genreService;

    @Mock
    ProductionStudioService productionStudioService;

    @Mock
    MovieEventPublisher movieEventPublisher;

    private MovieModel generateMovie() {
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());
        movieModel.setDeleteStatus(DeleteStatus.ACTIVE);
        movieModel.setOriginalTitle("Movie title");
        movieModel.setTranslateTitle("Movie title translate");
        return movieModel;
    }

    @Test
    void saveTest_HappyPath() {
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());
        when(movieRepository.save(any(MovieModel.class))).thenReturn(movieModel);

        final var response = movieService.save(movieModel);
        assertNotNull(response);
        assertEquals(MovieModel.class, response.getClass());
        assertEquals(movieModel.getMovieId(), response.getMovieId());
    }

    @Test
    void findActiveByIdTest_HappyPath() {
        when(movieRepository.findActiveById(any(UUID.class))).thenReturn(Optional.of(new MovieModel()));
        final var response = movieService.findActiveById(UUID.randomUUID());
        assertNotNull(response);
        assertEquals(MovieModel.class, response.getClass());

        verify(movieRepository, times(1)).findActiveById(any(UUID.class));
    }

    @Test
    void findActiveByIdTest_MovieNotFound() {
        when(movieRepository.findActiveById(any(UUID.class))).thenReturn(Optional.empty());
        UUID movieId = UUID.randomUUID();
        try {
            movieService.findActiveById(movieId);
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
            assertEquals("Movie not found with id: " + movieId, e.getMessage());
        }

        verify(movieRepository, times(1)).findActiveById(any(UUID.class));
    }

    @Test
    void findAllTest_HappyPath() {
        Page<MovieModel> page = new PageImpl<>(List.of(new MovieModel(), new MovieModel()));
        Pageable pageable = PageRequest.of(0, 10);
        Specification<MovieModel> spec = ((root, query, criteriaBuilder) -> null);

        when(movieRepository.findAll(spec, pageable)).thenReturn(page);

        final var response = movieService.findAllActive(spec, pageable);
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(MovieModel.class, response.getContent().getFirst().getClass());

        verify(movieRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void updateGenresTest_HappyPath() {
        final var genreModel = new GenreModel();
        genreModel.setGenreId(UUID.randomUUID());
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());

        when(genreService.findById(any(UUID.class))).thenReturn(genreModel);
        when(movieRepository.save(any(MovieModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var response = movieService.updateGenres(movieModel, Set.of(genreModel.getGenreId()));
        final var responseGenre = response.getGenres().stream().findFirst().orElse(null);
        assertNotNull(response);
        assertNotNull(responseGenre);
        assertEquals(MovieModel.class, response.getClass());
        assertEquals(movieModel.getMovieId(), response.getMovieId());
        assertEquals(genreModel.getGenreId(), responseGenre.getGenreId());
        verify(genreService, times(1)).findById(any(UUID.class));
        verify(movieRepository, times(1)).save(any(MovieModel.class));
    }

    @Test
    void updateProductionStudioTest_HappyPath() {
        final var productionStudioModel = new ProductionStudioModel();
        productionStudioModel.setProductionStudioId(UUID.randomUUID());
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());

        when(productionStudioService.findById(any(UUID.class))).thenReturn(productionStudioModel);
        when(movieRepository.save(any(MovieModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var response = movieService.updateProductionStudio(movieModel, productionStudioModel.getProductionStudioId());
        assertNotNull(response);
        assertNotNull(response.getProductionStudio());
        assertEquals(MovieModel.class, response.getClass());
        assertEquals(movieModel.getMovieId(), response.getMovieId());
        assertEquals(productionStudioModel.getProductionStudioId(), response.getProductionStudio().getProductionStudioId());
        verify(productionStudioService, times(1)).findById(any(UUID.class));
        verify(movieRepository, times(1)).save(any(MovieModel.class));
    }

    @Test
    void saveMovieTest_HappyPath() {
        RentalPriceModel rentalPriceModel = new RentalPriceModel(UUID.randomUUID(), BigDecimal.valueOf(15.00), LocalDateTime.now());
        final var movieModel = generateMovie();
        movieModel.getRentalPrices().add(rentalPriceModel);

        when(movieRepository.save(any(MovieModel.class))).thenReturn(movieModel);
        doNothing().when(movieEventPublisher).publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.CREATE);

        final var response = movieService.saveMovie(movieModel);
        assertNotNull(response);
        assertEquals(MovieModel.class, response.getClass());
        assertEquals(movieModel.getMovieId(), response.getMovieId());
        verify(movieRepository, times(1)).save(any(MovieModel.class));
        verify(movieEventPublisher, times(1)).publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.CREATE);
    }

    @Test
    void updateMovieTest_HappyPath() {
        RentalPriceModel rentalPriceModel = new RentalPriceModel(UUID.randomUUID(), BigDecimal.valueOf(15.00), LocalDateTime.now());
        final var movieModel = generateMovie();
        movieModel.getRentalPrices().add(rentalPriceModel);
        when(movieRepository.save(any(MovieModel.class))).thenReturn(movieModel);
        doNothing().when(movieEventPublisher).publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.UPDATE);

        final var response = movieService.updateMovie(movieModel);
        assertNotNull(response);
        assertEquals(MovieModel.class, response.getClass());
        assertEquals(movieModel.getMovieId(), response.getMovieId());
        verify(movieRepository, times(1)).save(any(MovieModel.class));
        verify(movieEventPublisher, times(1)).publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.UPDATE);
    }

    @Test
    void deleteMovieTest_HappyPath() {
        RentalPriceModel rentalPriceModel = new RentalPriceModel(UUID.randomUUID(), BigDecimal.valueOf(15.00), LocalDateTime.now());
        final var movieModel = generateMovie();
        movieModel.getRentalPrices().add(rentalPriceModel);
        when(movieRepository.save(any(MovieModel.class))).thenReturn(movieModel);
        doNothing().when(movieEventPublisher).publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.DELETE);

        movieService.deleteMovie(movieModel);

        verify(movieRepository, times(1)).save(any(MovieModel.class));
        verify(movieEventPublisher, times(1)).publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.DELETE);
    }

}