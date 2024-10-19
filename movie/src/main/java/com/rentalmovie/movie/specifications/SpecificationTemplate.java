package com.rentalmovie.movie.specifications;

import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

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

    public static<T> Specification<T> hasActiveStatus() {
        return (root, query, cb) -> cb.equal(root.get("deleteStatus"), DeleteStatus.ACTIVE);
    }
}
