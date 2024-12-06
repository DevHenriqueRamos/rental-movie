package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.enums.ActionType;
import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.publishers.MovieEventPublisher;
import com.rentalmovie.movie.repositories.MovieRepository;
import com.rentalmovie.movie.services.GenreService;
import com.rentalmovie.movie.services.MovieService;
import com.rentalmovie.movie.services.ProductionStudioService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final GenreService genreService;

    private final ProductionStudioService productionStudioService;

    private final MovieEventPublisher movieEventPublisher;

    public MovieServiceImpl(MovieRepository movieRepository, GenreService genreService, ProductionStudioService productionStudioService, MovieEventPublisher movieEventPublisher) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
        this.productionStudioService = productionStudioService;
        this.movieEventPublisher = movieEventPublisher;
    }

    @Override
    public MovieModel save(MovieModel movieModel) {
        return movieRepository.save(movieModel);
    }

    @Override
    public MovieModel findActiveById(UUID movieId) {
        return movieRepository.findActiveById(movieId)
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found with id: " + movieId));
    }

    @Override
    public Page<MovieModel> findAllActive(Specification<MovieModel> specification, Pageable pageable) {
        return movieRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional
    public MovieModel updateGenres(MovieModel movieModel, Set<UUID> genreIds) {
        var setGenres = new HashSet<GenreModel>();
        for (UUID genreId : genreIds) {
            GenreModel genreModel = genreService.findById(genreId);
            setGenres.add(genreModel);
        }
        movieModel.setGenres(setGenres);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return movieRepository.save(movieModel);
    }

    @Override
    @Transactional
    public MovieModel updateProductionStudio(MovieModel movieModel, UUID productionStudioId) {
        ProductionStudioModel productionStudioModel = productionStudioService.findById(productionStudioId);
        movieModel.setProductionStudio(productionStudioModel);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return movieRepository.save(movieModel);
    }


    @Override
    @Transactional
    public MovieModel saveMovie(MovieModel movieModel) {
        movieModel = save(movieModel);
        movieEventPublisher.publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.CREATE);
        return movieModel;
    }

    @Override
    @Transactional
    public MovieModel updateMovie(MovieModel movieModel) {
        movieModel = save(movieModel);
        movieEventPublisher.publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.UPDATE);
        return movieModel;
    }

    @Override
    @Transactional
    public void deleteMovie(MovieModel movieModel) {
        movieModel = save(movieModel);
        movieEventPublisher.publishMovieEvent(movieModel.convertToMovieEventDTO(), ActionType.DELETE);
    }
}
