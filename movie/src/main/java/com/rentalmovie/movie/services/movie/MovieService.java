package com.rentalmovie.movie.services.movie;

import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.repositories.MovieRepository;
import com.rentalmovie.movie.services.genre.GenreService;
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
import java.util.*;

@Service
public class MovieService implements IMovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreService genreService;

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
}
