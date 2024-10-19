package com.rentalmovie.movie.services.genre;

import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GenreService implements IGenreService{

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public Optional<GenreModel> findById(UUID genreId) {
        return genreRepository.findById(genreId);
    }

    @Override
    public GenreModel save(GenreModel genreModel) {
        return genreRepository.save(genreModel);
    }

    @Override
    public Page<GenreModel> findAllActive(Specification<GenreModel> specification, Pageable pageable) {
        return genreRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<GenreModel> findActiveById(UUID genreId) {
        return genreRepository.findActiveById(genreId);
    }
}
