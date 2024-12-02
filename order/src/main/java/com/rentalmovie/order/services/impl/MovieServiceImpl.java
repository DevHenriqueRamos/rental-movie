package com.rentalmovie.order.services.impl;

import com.rentalmovie.order.models.MovieModel;
import com.rentalmovie.order.repositories.MovieRepository;
import com.rentalmovie.order.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    MovieRepository movieRepository;

    @Override
    public Optional<MovieModel> findById(UUID movieId) {
        return movieRepository.findById(movieId);
    }

    @Override
    public Set<MovieModel> getMovieSet(Set<UUID> moviesIds) {
        return moviesIds.stream()
                .map(movieRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public MovieModel save(MovieModel movieModel) {
        return movieRepository.save(movieModel);
    }

    @Override
    public void delete(MovieModel movieModel) {
        movieRepository.delete(movieModel);
    }
}
