package com.rentalmovie.movie.specifications;

import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import jakarta.persistence.criteria.Join;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "releaseYear", spec = Equal.class),
            @Spec(path = "ageRange", spec = Equal.class),
            @Spec(path = "rating", spec = Equal.class)
    })
    public interface MovieSpecification extends Specification<MovieModel> {}

    @Spec(path = "name", spec = Like.class)
    public interface ProductionStudioSpecification extends Specification<ProductionStudioModel> {}

    @Spec(path = "name", spec = Like.class)
    public interface GenreSpecification extends Specification<GenreModel> {}

    public static Specification<MovieModel> hasActiveStatus() {
        return (root, query, cb) -> cb.equal(root.get("deleteStatus"), DeleteStatus.ACTIVE);
    }

    public static Specification<MovieModel> hasProductionStudio(UUID productionStudioId) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<MovieModel, ProductionStudioModel> studioProd = root.join("productionStudio");
            return cb.equal(studioProd.get("productionStudioId"), productionStudioId);
        };
    }

    public static Specification<MovieModel> hasGenres(List<UUID> genreIds) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            if(genreIds == null || genreIds.isEmpty()) {
                return cb.conjunction();
            }
            Join<MovieModel, GenreModel> genreProd = root.join("genres");
            return genreProd.get("genreId").in(genreIds);
        };
    }
}
