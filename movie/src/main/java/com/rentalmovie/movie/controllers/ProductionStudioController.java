package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.dtos.ProductionStudioDTO;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.services.ProductionStudioService;
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
@RequestMapping("/production-studios")
public class ProductionStudioController {

    @Autowired
    ProductionStudioService productionStudioService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductionStudioModel> save(@RequestBody @Valid ProductionStudioDTO productionStudioDTO ){
        var productionStudioModel = new ProductionStudioModel();
        BeanUtils.copyProperties(productionStudioDTO, productionStudioModel);
        productionStudioModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(productionStudioService.save(productionStudioModel));
    }

    @GetMapping
    public ResponseEntity<Page<ProductionStudioModel>> findAll(
            SpecificationTemplate.ProductionStudioSpecification specification,
            @PageableDefault(page = 0, size = 10, sort = "productionStudioId", direction = Sort.Direction.ASC) Pageable pageable
    ){
        Page<ProductionStudioModel> productionStudioModelPage =
                productionStudioService.findAll(specification, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(productionStudioModelPage);
    }

    @GetMapping("/{productionStudioId}")
    public ResponseEntity<Object> getById(@PathVariable UUID productionStudioId) {
        Optional<ProductionStudioModel> productionStudioModelOptional = productionStudioService.findById(productionStudioId);
        if(productionStudioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Production studio not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(productionStudioModelOptional.get());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{productionStudioId}")
    public ResponseEntity<Object> update(@PathVariable UUID productionStudioId, @RequestBody @Valid ProductionStudioDTO productionStudioDTO) {
        Optional<ProductionStudioModel> productionStudioModelOptional = productionStudioService.findById(productionStudioId);
        if(productionStudioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Production studio not found");
        }
        var productionStudioModel = productionStudioModelOptional.get();
        BeanUtils.copyProperties(productionStudioDTO, productionStudioModel);
        return ResponseEntity.status(HttpStatus.OK).body(productionStudioService.save(productionStudioModel));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{productionStudioId}")
    public ResponseEntity<Object> delete(@PathVariable UUID productionStudioId) {
        Optional<ProductionStudioModel> productionStudioModelOptional = productionStudioService.findById(productionStudioId);
        if(productionStudioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Production studio not found");
        }
        productionStudioService.delete(productionStudioModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Production studio deleted successfully");
    }
}
