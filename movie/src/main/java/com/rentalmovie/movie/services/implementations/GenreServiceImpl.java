package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.exceptions.ResourceNotFoundException;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.repositories.GenreRepository;
import com.rentalmovie.movie.services.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public GenreModel findById(UUID genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(()-> new ResourceNotFoundException("Genre not found with id: " + genreId));
    }

    @Override
    public GenreModel save(GenreModel genreModel) {
        return genreRepository.save(genreModel);
    }

    @Override
    public Page<GenreModel> findAll(Specification<GenreModel> specification, Pageable pageable) {
        return genreRepository.findAll(specification, pageable);
    }

    @Override
    public void delete(GenreModel genreModel) {
        genreRepository.delete(genreModel);
    }
}
