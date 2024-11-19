package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.dtos.GenreDTO;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.services.GenreService;
import com.rentalmovie.movie.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    GenreService genreService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GenreModel> save(@RequestBody @Valid GenreDTO genreDTO) {
        var genreModel = new GenreModel();
        BeanUtils.copyProperties(genreDTO, genreModel);
        genreModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.save(genreModel));
    }

    @GetMapping
    public ResponseEntity<Page<GenreModel>> findAll(
            SpecificationTemplate.GenreSpecification specification,
            @PageableDefault(page = 0, size = 10, sort = "genreId", direction = Sort.Direction.ASC) Pageable pageable
    ){
        Page<GenreModel> genreModelPage =
                genreService.findAll(specification, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(genreModelPage);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<Object> getById(@PathVariable UUID genreId) {
        Optional<GenreModel> genreModelOptional = genreService.findById(genreId);
        if(genreModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Genre not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(genreModelOptional.get());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{genreId}")
    public ResponseEntity<Object> update(@PathVariable UUID genreId, @RequestBody @Valid GenreDTO genreDTO) {
        Optional<GenreModel> genreModelOptional = genreService.findById(genreId);
        if(genreModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Genre not found");
        }
        var productionStudioModel = genreModelOptional.get();
        BeanUtils.copyProperties(genreDTO, productionStudioModel);
        return ResponseEntity.status(HttpStatus.OK).body(genreService.save(productionStudioModel));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{genreId}")
    public ResponseEntity<Object> delete(@PathVariable UUID genreId) {
        Optional<GenreModel> genreModelOptional = genreService.findById(genreId);
        if(genreModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Genre not found");
        }
        genreService.delete(genreModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Genre deleted successfully");
    }
}
