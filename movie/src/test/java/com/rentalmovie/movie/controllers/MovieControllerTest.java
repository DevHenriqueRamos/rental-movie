package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.models.RentalPriceModel;
import com.rentalmovie.movie.repositories.GenreRepository;
import com.rentalmovie.movie.repositories.MovieRepository;
import com.rentalmovie.movie.repositories.ProductionStudioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static com.rentalmovie.movie.testutils.JwtTokenUtil.generateToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ProductionStudioRepository productionStudioRepository;

    @Autowired
    GenreRepository genreRepository;

    private ProductionStudioModel generateProductionStudioModel() {
        ProductionStudioModel productionStudioModel = new ProductionStudioModel();
        productionStudioModel.setProductionStudioId(UUID.randomUUID());
        productionStudioModel.setName("Production Studio test");
        productionStudioModel.setDescription("Production studio description test");
        productionStudioModel.setCreationDate(LocalDateTime.now());
        return productionStudioRepository.save(productionStudioModel);
    }

    private GenreModel generateGenreModel() {
        GenreModel genreModel = new GenreModel();
        genreModel.setGenreId(UUID.randomUUID());
        genreModel.setName("Genre test");
        genreModel.setDescription("Genre description test");
        genreModel.setCreationDate(LocalDateTime.now());
        return genreRepository.save(genreModel);
    }

    private MovieModel generateMovie() {
        final var movieModel = new MovieModel();
        movieModel.setMovieId(UUID.randomUUID());
        movieModel.setDeleteStatus(DeleteStatus.ACTIVE);
        movieModel.setOriginalTitle("Movie title");
        movieModel.setTranslateTitle("Movie title translate");
        movieModel.setCreationDate(LocalDateTime.now());
        movieModel.setLastUpdateDate(LocalDateTime.now());
        movieModel.setSynopsis("Movie synopsis");
        movieModel.setMovieUrl("Movie url");
        movieModel.setAgeRange(16);
        movieModel.setReleaseYear(2024);
        movieModel.addRentalPrice(BigDecimal.valueOf(15.00));
        return movieRepository.save(movieModel);
    }

    @Test
    void getByIdTest_HappyPath() throws Exception {
        final var movie = generateMovie();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/movies/{movieId}", movie.getMovieId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value(movie.getMovieId().toString()))
                .andExpect(jsonPath("$.originalTitle").value(movie.getOriginalTitle()));
    }

    @Test
    void getAllTest_HappyPath() throws Exception {
        generateMovie();
        generateMovie();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/movies").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllTest_HappyPathWithAllSpecifications() throws Exception {
        final var productionStudioModel = generateProductionStudioModel();
        final var genreModel = generateGenreModel();

        final var movieModel1 = generateMovie();
        movieModel1.setProductionStudio(productionStudioModel);
        movieModel1.getGenres().add(genreModel);
        movieModel1.setAgeRange(18);
        movieModel1.setReleaseYear(2022);
        movieRepository.save(movieModel1);

        // create more 2 movies on db
        generateMovie();
        generateMovie();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String spec = "?releaseYear=2022&ageRange=18&studioId=%s&genreIds=%s"
                .formatted(productionStudioModel.getProductionStudioId(), genreModel.getGenreId());

        mockMvc.perform(get("/movies" + spec).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].movieId").value(movieModel1.getMovieId().toString()))
                .andExpect(jsonPath("$.content[0].ageRange").value(18))
                .andExpect(jsonPath("$.content[0].releaseYear").value(2022));
    }

    @Test
    void saveTest_HappyPath() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String movieDTO = """
            {
                "originalTitle":"Movie title",
                "translateTitle":"Movie title translate",
                "synopsis":"Movie synopsis",
                "releaseYear":2022,
                "ageRange":18,
                "durationMinutes":104,
                "rating":6.7,
                "price": 15.00,
                "coverUrl": "movie cover url",
                "movieUrl": "movie url"
            }
        """;

        mockMvc.perform(post("/movies").header("Authorization", token)
                .contentType(APPLICATION_JSON)
                .content(movieDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalTitle").value("Movie title"))
                .andExpect(jsonPath("$.translateTitle").value("Movie title translate"))
                .andExpect(jsonPath("$.releaseYear").value(2022))
                .andExpect(jsonPath("$.ageRange").value(18));

        List<MovieModel> movies = movieRepository.findAll();
        assertEquals(1, movies.size());
        assertEquals("Movie title", movies.getFirst().getOriginalTitle());
        assertEquals(6.7, movies.getFirst().getRating());
        assertEquals(2022, movies.getFirst().getReleaseYear());
    }

    @Test
    void saveTest_IncompleteJsonToSaveNewMovie() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String movieDTO = """
            {
                "translateTitle":"Movie title translate",
                "synopsis":"Movie synopsis",
                "releaseYear":2022,
                "ageRange":18,
                "durationMinutes":104,
                "rating":6.7,
                "coverUrl": "movie cover url",
                "movieUrl": "movie url"
            }
        """;

        mockMvc.perform(post("/movies").header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content(movieDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.originalTitle").value("must not be blank"))
                .andExpect(jsonPath("$.errors.price").value("must not be null"));
    }

    @Test
    void updateMovieTest_HappyPath() throws Exception {
        final var movieModel = generateMovie();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String movieDTO = """
            {
                "originalTitle":"Movie title update",
                "translateTitle":"Movie title translate update",
                "synopsis":"Movie synopsis",
                "releaseYear":2024,
                "ageRange":16,
                "durationMinutes":104,
                "rating":6.7,
                "coverUrl": "movie cover url",
                "movieUrl": "movie url"
            }
        """;

        mockMvc.perform(put("/movies/update/{movieId}", movieModel.getMovieId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content(movieDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalTitle").value("Movie title update"))
                .andExpect(jsonPath("$.translateTitle").value("Movie title translate update"))
                .andExpect(jsonPath("$.releaseYear").value(2024))
                .andExpect(jsonPath("$.ageRange").value(16));
    }

    @Test
    void updateProductionStudioTest_HappyPath() throws Exception {
        final var movieModel = generateMovie();
        LocalDateTime currentLastUpdate = movieModel.getLastUpdateDate();
        final var productionStudioModel = generateProductionStudioModel();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(put("/movies/update/{movieId}/production-studio/{productionStudioId}",
                        movieModel.getMovieId(), productionStudioModel.getProductionStudioId()).header("Authorization", token))
                .andExpect(status().isOk());

        Optional<MovieModel> movieUpdated = movieRepository.findById(movieModel.getMovieId());
        assertTrue(movieUpdated.isPresent());
        assertNotEquals(currentLastUpdate, movieUpdated.get().getLastUpdateDate());
        assertEquals(movieUpdated.get().getProductionStudio().getProductionStudioId(), productionStudioModel.getProductionStudioId());
    }

    @Test
    void updateProductionStudioTest_CannotUpdateMovieIfNotFoundProductionStudio() throws Exception {
        final var movieModel = generateMovie();
        LocalDateTime currentLastUpdate = movieModel.getLastUpdateDate();
        UUID productionStudioId = UUID.randomUUID();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(put("/movies/update/{movieId}/production-studio/{productionStudioId}",
                        movieModel.getMovieId(), productionStudioId).header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Production studio not found with id: " + productionStudioId));

        Optional<MovieModel> movieUpdated = movieRepository.findById(movieModel.getMovieId());
        assertTrue(movieUpdated.isPresent());
        assertEquals(currentLastUpdate, movieUpdated.get().getLastUpdateDate());
        assertNull(movieUpdated.get().getProductionStudio());
    }

    @Test
    void updateGenresTest_HappyPath() throws Exception {
        final var movieModel = generateMovie();
        LocalDateTime currentLastUpdate = movieModel.getLastUpdateDate();
        final var genreModel = generateGenreModel();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String genreDTO = """
            {
                "genres":["%s"]
            }
        """.formatted(genreModel.getGenreId());

        mockMvc.perform(put("/movies/update/{movieId}/genres", movieModel.getMovieId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON).content(genreDTO))
                .andExpect(status().isOk());

        Optional<MovieModel> movieUpdated = movieRepository.findById(movieModel.getMovieId());
        assertTrue(movieUpdated.isPresent());
        final var genreOnMovie = movieUpdated.get().getGenres().stream().findFirst().orElse(null);
        assertNotNull(genreOnMovie);
        assertNotEquals(currentLastUpdate, movieUpdated.get().getLastUpdateDate());
        assertEquals(1, movieUpdated.get().getGenres().size());
        assertEquals(genreModel.getGenreId(), genreOnMovie.getGenreId());
    }

    @Test
    void updateGenresTest_CannotUpdateMovieIfNotFoundGenre() throws Exception {
        final var movieModel = generateMovie();
        LocalDateTime currentLastUpdate = movieModel.getLastUpdateDate();
        UUID genreId = UUID.randomUUID();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String genreDTO = """
            {
                "genres":["%s"]
            }
        """.formatted(genreId);

        mockMvc.perform(put("/movies/update/{movieId}/genres", movieModel.getMovieId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON).content(genreDTO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Genre not found with id: " + genreId));

        Optional<MovieModel> movieUpdated = movieRepository.findById(movieModel.getMovieId());
        assertTrue(movieUpdated.isPresent());
        assertEquals(currentLastUpdate, movieUpdated.get().getLastUpdateDate());
        assertTrue(movieUpdated.get().getGenres().isEmpty());
    }

    @Test
    void updateGenresTest_InCaseAnyoneGenreIdIsPassed() throws Exception {
        final var movieModel = generateMovie();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String genreDTO = """
            {
                "genres":[]
            }
        """;

        mockMvc.perform(put("/movies/update/{movieId}/genres", movieModel.getMovieId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON).content(genreDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Genres cannot be null"));
    }

    @Test
    void updateGenresTest_SendRequisitionWithoutGenreSet() throws Exception {
        final var movieModel = generateMovie();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(put("/movies/update/{movieId}/genres", movieModel.getMovieId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.genres").value("must not be null"));
    }

    @Test
    void updatePriceTest_HappyPath() throws Exception {
        final var movieModel = generateMovie();

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String priceDTO = """
                {"price": 25.00}
                """;

        mockMvc.perform(put("/movies/update/{movieId}/price", movieModel.getMovieId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON).content(priceDTO))
                .andExpect(status().isOk());

        Optional<MovieModel> movieUpdated = movieRepository.findById(movieModel.getMovieId());
        assertTrue(movieUpdated.isPresent());
        final var moviePrices = movieUpdated.get().getRentalPrices();
        assertEquals(2, moviePrices.size());
        Optional<RentalPriceModel> lastPriceAdded = moviePrices.stream().max(Comparator.comparing(RentalPriceModel::getCreationDate));
        assertTrue(lastPriceAdded.isPresent());
        assertEquals(BigDecimal.valueOf(25.0).setScale(2, RoundingMode.HALF_UP), lastPriceAdded.get().getPrice()
        );
    }

    @Test
    void updatePriceTest_SendRequisitionWithoutPrice() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(put("/movies/update/{movieId}/price", UUID.randomUUID()).header("Authorization", token)
                        .contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.price").value("must not be null"));
    }

    @Test
    void deleteTest_HappyPath() throws Exception {
        final var movieModel = generateMovie();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(delete("/movies/delete/{movieId}", movieModel.getMovieId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Movie deleted successfully"));

        Optional<MovieModel> movieUpdated = movieRepository.findById(movieModel.getMovieId());
        assertTrue(movieUpdated.isPresent());
        assertEquals(movieModel.getMovieId(), movieUpdated.get().getMovieId());
        assertEquals(DeleteStatus.INACTIVE, movieUpdated.get().getDeleteStatus());
    }

    @Test
    void movieNotFoundTest() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);
        UUID movieId = UUID.randomUUID();

        mockMvc.perform(delete("/movies/delete/{movieId}", movieId).header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movie not found with id: " + movieId));
    }

    @Test
    void userWithRoleConsumerCantAccessAdminMethodTest() throws Exception {
        final var movieId = UUID.randomUUID();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_CONSUMER", 60000);

        mockMvc.perform(delete("/movies/delete/{movieId}", movieId)
                        .header("Authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void invalidTokenTest() throws Exception {
        String token = "invalidToken";

        UUID randomUUID = UUID.randomUUID();

        mockMvc.perform(get("/movies/{movieId}", randomUUID)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"));
    }

}