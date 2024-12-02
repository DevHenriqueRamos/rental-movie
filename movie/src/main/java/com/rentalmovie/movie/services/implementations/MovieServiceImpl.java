package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.enums.ActionType;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.publishers.MovieEventPublisher;
import com.rentalmovie.movie.repositories.MovieRepository;
import com.rentalmovie.movie.services.GenreService;
import com.rentalmovie.movie.services.MovieService;
import com.rentalmovie.movie.services.ProductionStudioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    GenreService genreService;

    @Autowired
    ProductionStudioService productionStudioService;

    @Autowired
    MovieEventPublisher movieEventPublisher;

    @Override
    public MovieModel save(MovieModel movieModel) {
        return movieRepository.save(movieModel);
    }

    @Override
    public Optional<MovieModel> findActiveById(UUID movieId) {
        return movieRepository.findActiveById(movieId);
    }

    @Override
    public Page<MovieModel> findAllActive(Specification<MovieModel> specification, Pageable pageable) {
        return movieRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional
    public Object updateGenres(MovieModel movieModel, Set<UUID> genreIds) {
        var setGenres = new HashSet<GenreModel>();
        for (UUID genreId : genreIds) {
            Optional<GenreModel> genreModelOptional = genreService.findById(genreId);
            genreModelOptional.ifPresent(setGenres::add);
        }
        System.out.println(setGenres);
        if (setGenres.isEmpty()) {
            return "Cannot update movie because genres not found";
        }
        movieModel.setGenres(setGenres);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return movieRepository.save(movieModel);
    }

    @Override
    @Transactional
    public Object updateProductionStudio(MovieModel movieModel, UUID productionStudioId) {
        Optional<ProductionStudioModel> productionStudioModelOptional = productionStudioService.findById(productionStudioId);
        if(productionStudioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot update movie because production studio not found");
        }
        movieModel.setProductionStudio(productionStudioModelOptional.get());
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
