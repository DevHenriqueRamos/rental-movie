package com.rentalmovie.movie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rentalmovie.movie.dtos.MovieDTO;
import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.services.genre.GenreService;
import com.rentalmovie.movie.services.movie.MovieService;
import com.rentalmovie.movie.services.productionstudio.ProductionStudioService;
import com.rentalmovie.movie.specifications.SpecificationTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ProductionStudioService productionStudioService;

    @Autowired
    private GenreService genreService;

    @PostMapping
    public ResponseEntity<MovieModel> save(
            @RequestBody
            @Validated(MovieDTO.MovieView.RegistrationPost.class)
            @JsonView(MovieDTO.MovieView.RegistrationPost.class)
            MovieDTO movieDTO
    ) {
        var movieModel = new MovieModel();
        BeanUtils.copyProperties(movieDTO, movieModel);
        movieModel.setDeleteStatus(DeleteStatus.ACTIVE);
        if(movieDTO.getProductionStudioId() != null) {
            movieModel.setProductionStudio(productionStudioService.findById(movieDTO.getProductionStudioId()));
        }
        if(movieDTO.getGenres() != null) {
            var setGenres = new HashSet<GenreModel>();
            for (UUID genreId : movieDTO.getGenres()) {
                Optional<GenreModel> genreModelOptional = genreService.findById(genreId);
                genreModelOptional.ifPresent(setGenres::add);
            }
            movieModel.setGenres(setGenres);
        }

        movieModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.save(movieModel));
    }

    @GetMapping
    public ResponseEntity<Page<MovieModel>> getAll(
            SpecificationTemplate.MovieSpecification specification,
            @PageableDefault(page = 0, size = 10, sort = "movieId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<MovieModel> movieModelPage = movieService.findAllActive(SpecificationTemplate.<MovieModel>hasActiveStatus().and(specification), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(movieModelPage);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Object> getById(@PathVariable UUID movieId) {
        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if(movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(movieModelOptional.get());
    }

    @PutMapping("/{movieId}")
    public ResponseEntity<Object> updateMovie(
            @PathVariable UUID movieId,
            @RequestBody
            @Validated(MovieDTO.MovieView.MoviePut.class)
            @JsonView(MovieDTO.MovieView.MoviePut.class)
            MovieDTO movieDTO
    ) {
        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if(movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        MovieModel movieModel = movieModelOptional.get();
        BeanUtils.copyProperties(movieDTO, movieModel);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(movieService.save(movieModel));
    }

    @PutMapping("/{movieId}/production-studio/{productionStudioId}")
    public ResponseEntity<Object> updateProductionStudio(
            @PathVariable UUID movieId,
            @PathVariable UUID productionStudioId
    ) {
        Optional<ProductionStudioModel> productionStudioModelOptional = productionStudioService.findActiveById(productionStudioId);
        if(productionStudioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot update movie because production studio not found");
        }
        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if(movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        MovieModel movieModel = movieModelOptional.get();
        movieModel.setProductionStudio(productionStudioModelOptional.get());
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(movieService.save(movieModel));
    }

    @PutMapping("/{movieId}/genres")
    public ResponseEntity<Object> updateProductionStudio(
            @PathVariable UUID movieId,
            @RequestBody
            @Validated(MovieDTO.MovieView.GenresPut.class)
            @JsonView(MovieDTO.MovieView.GenresPut.class)
            MovieDTO movieDTO
    ) {
        if(movieDTO.getGenres() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Genres cannot be null");
        }

        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if(movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(movieService.updateGenres(movieModelOptional.get(), movieDTO.getGenres()));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Object> delete(@PathVariable UUID movieId) {
        Optional<MovieModel> movieModelOptional = movieService.findActiveById(movieId);
        if(movieModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }
        MovieModel movieModel = movieModelOptional.get();
        movieModel.setDeleteStatus(DeleteStatus.INACTIVE);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        movieService.save(movieModel);
        return ResponseEntity.status(HttpStatus.OK).body("Movie deleted successfully");
    }
}
