package com.rentalmovie.movie.services.implementations;

import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.repositories.GenreRepository;
import com.rentalmovie.movie.services.GenreService;
import com.rentalmovie.movie.specifications.SpecificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GenreServiceImpl implements GenreService {

    @Autowired
    GenreRepository genreRepository;

    @Override
    public Optional<GenreModel> findById(UUID genreId) {
        return genreRepository.findById(genreId);
    }

    @Override
    public GenreModel save(GenreModel genreModel) {
        return genreRepository.save(genreModel);
    }

    @Override
    public Page<GenreModel> findAll(SpecificationTemplate.GenreSpecification specification, Pageable pageable) {
        return genreRepository.findAll(specification, pageable);
    }

    @Override
    public void delete(GenreModel genreModel) {
        genreRepository.delete(genreModel);
    }
}
