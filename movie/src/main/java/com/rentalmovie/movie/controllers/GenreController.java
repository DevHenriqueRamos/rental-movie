package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.dtos.GenreDTO;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.services.GenreService;
import com.rentalmovie.movie.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
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
import java.util.UUID;

import static com.rentalmovie.movie.utils.ResponseUtils.createMessageResponse;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GenreModel> save(@RequestBody @Valid GenreDTO genreDTO) {
        var genreModel = new GenreModel();
        BeanUtils.copyProperties(genreDTO, genreModel);
        genreModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.save(genreModel));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping
    public ResponseEntity<Page<GenreModel>> getAll(
            SpecificationTemplate.GenreSpecification specification,
            @PageableDefault(sort = "genreId", direction = Sort.Direction.ASC) Pageable pageable
    ){
        Page<GenreModel> genreModelPage =
                genreService.findAll(specification, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(genreModelPage);
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/{genreId}")
    public ResponseEntity<Object> getById(@PathVariable UUID genreId) {
        GenreModel genreModel = genreService.findById(genreId);
        return ResponseEntity.status(HttpStatus.OK).body(genreModel);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{genreId}")
    public ResponseEntity<Object> update(@PathVariable UUID genreId, @RequestBody @Valid GenreDTO genreDTO) {
        GenreModel genreModel = genreService.findById(genreId);
        BeanUtils.copyProperties(genreDTO, genreModel);
        return ResponseEntity.status(HttpStatus.OK).body(genreService.save(genreModel));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/delete/{genreId}")
    public ResponseEntity<Object> delete(@PathVariable UUID genreId) {
        GenreModel genreModel = genreService.findById(genreId);
        genreService.delete(genreModel);
        return ResponseEntity.status(HttpStatus.OK).body(createMessageResponse("Genre deleted successfully"));
    }
}
