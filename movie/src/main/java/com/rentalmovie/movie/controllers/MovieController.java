package com.rentalmovie.movie.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rentalmovie.movie.dtos.MovieDTO;
import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.services.MovieService;
import com.rentalmovie.movie.specifications.SpecificationTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.rentalmovie.movie.utils.ResponseUtils.createMessageResponse;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
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
        movieModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        movieModel.addRentalPrice(movieDTO.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.saveMovie(movieModel));
    }

    @GetMapping
    public ResponseEntity<Page<MovieModel>> getAll(
            SpecificationTemplate.MovieSpecification specification,
            @PageableDefault(sort = "movieId", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) UUID studioId,
            @RequestParam(required = false)List<UUID> genreIds
    ) {
        //Create Specification
        Specification<MovieModel> finalSpec = SpecificationTemplate.hasActiveStatus().and(specification);
        finalSpec = studioSpec(finalSpec, studioId);
        finalSpec = !CollectionUtils.isEmpty(genreIds) ? finalSpec.and(SpecificationTemplate.hasGenres(genreIds)) : finalSpec;

        Page<MovieModel> movieModelPage = movieService.findAllActive(finalSpec, pageable);
        // Create hyperlink for each movie
        movieModelPage
                .forEach(movie -> movie.add(linkTo(methodOn(MovieController.class).getById(movie.getMovieId())).withSelfRel()));
        return ResponseEntity.status(HttpStatus.OK).body(movieModelPage);
    }

    private Specification<MovieModel> studioSpec(Specification<MovieModel> finalSpec, UUID studioId) {
        if (studioId != null) {
            finalSpec = finalSpec.and(SpecificationTemplate.hasProductionStudio(studioId));
        }
        return finalSpec;
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/{movieId}")
    public ResponseEntity<Object> getById(@PathVariable UUID movieId) {
        MovieModel movieModel = movieService.findActiveById(movieId);
        return ResponseEntity.status(HttpStatus.OK).body(movieModel);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<Object> updateMovie(
            @PathVariable UUID movieId,
            @RequestBody
            @Validated(MovieDTO.MovieView.MoviePut.class)
            @JsonView(MovieDTO.MovieView.MoviePut.class)
            MovieDTO movieDTO
    ) {
        MovieModel movieModel = movieService.findActiveById(movieId);
        BeanUtils.copyProperties(movieDTO, movieModel);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(movieService.updateMovie(movieModel));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{movieId}/production-studio/{productionStudioId}")
    public ResponseEntity<Object> updateProductionStudio(
            @PathVariable UUID movieId,
            @PathVariable UUID productionStudioId
    ) {
        MovieModel movieModel = movieService.findActiveById(movieId);
        return ResponseEntity.status(HttpStatus.OK).body(movieService.updateProductionStudio(movieModel, productionStudioId));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{movieId}/genres")
    public ResponseEntity<Object> updateGenres(
            @PathVariable UUID movieId,
            @RequestBody
            @Validated(MovieDTO.MovieView.GenresPut.class)
            @JsonView(MovieDTO.MovieView.GenresPut.class)
            MovieDTO movieDTO
    ) {
        ResponseEntity<Object> response;
        if(movieDTO.getGenres().isEmpty()) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createMessageResponse("Genres cannot be null"));
        } else {
            MovieModel movieModel = movieService.findActiveById(movieId);
            response = ResponseEntity.status(HttpStatus.OK).body(movieService.updateGenres(movieModel, movieDTO.getGenres()));
        }
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{movieId}/price")
    public ResponseEntity<Object> updatePrice(
            @PathVariable UUID movieId,
            @RequestBody
            @Validated(MovieDTO.MovieView.PricePut.class)
            @JsonView(MovieDTO.MovieView.PricePut.class)
            MovieDTO movieDTO
    ) {
        MovieModel movieModel = movieService.findActiveById(movieId);
        movieModel.addRentalPrice(movieDTO.getPrice());
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(movieService.updateMovie(movieModel));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<Object> delete(@PathVariable UUID movieId) {
        MovieModel movieModel = movieService.findActiveById(movieId);
        movieModel.setDeleteStatus(DeleteStatus.INACTIVE);
        movieModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        movieService.deleteMovie(movieModel);
        return ResponseEntity.status(HttpStatus.OK).body(createMessageResponse("Movie deleted successfully"));
    }
}
