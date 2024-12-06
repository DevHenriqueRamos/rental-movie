package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.dtos.ProductionStudioDTO;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.services.ProductionStudioService;
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
@RequestMapping("/production-studios")
public class ProductionStudioController {

    private final ProductionStudioService productionStudioService;

    public ProductionStudioController(ProductionStudioService productionStudioService) {
        this.productionStudioService = productionStudioService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductionStudioModel> save(@RequestBody @Valid ProductionStudioDTO productionStudioDTO ){
        var productionStudioModel = new ProductionStudioModel();
        BeanUtils.copyProperties(productionStudioDTO, productionStudioModel);
        productionStudioModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(productionStudioService.save(productionStudioModel));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping
    public ResponseEntity<Page<ProductionStudioModel>> getAll(
            SpecificationTemplate.ProductionStudioSpecification specification,
            @PageableDefault(sort = "productionStudioId", direction = Sort.Direction.ASC) Pageable pageable
    ){
        Page<ProductionStudioModel> productionStudioModelPage =
                productionStudioService.findAll(specification, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(productionStudioModelPage);
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/{productionStudioId}")
    public ResponseEntity<Object> getById(@PathVariable UUID productionStudioId) {
        ProductionStudioModel productionStudioModel = productionStudioService.findById(productionStudioId);
        return ResponseEntity.status(HttpStatus.OK).body(productionStudioModel);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/update/{productionStudioId}")
    public ResponseEntity<Object> update(@PathVariable UUID productionStudioId, @RequestBody @Valid ProductionStudioDTO productionStudioDTO) {
        ProductionStudioModel productionStudioModel = productionStudioService.findById(productionStudioId);
        BeanUtils.copyProperties(productionStudioDTO, productionStudioModel);
        return ResponseEntity.status(HttpStatus.OK).body(productionStudioService.save(productionStudioModel));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/delete/{productionStudioId}")
    public ResponseEntity<Object> delete(@PathVariable UUID productionStudioId) {
        ProductionStudioModel productionStudioModel = productionStudioService.findById(productionStudioId);
        productionStudioService.delete(productionStudioModel);
        return ResponseEntity.status(HttpStatus.OK).body(createMessageResponse("Production studio deleted successfully"));
    }
}
