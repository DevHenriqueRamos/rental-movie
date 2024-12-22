package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.enums.DeleteStatus;
import com.rentalmovie.movie.models.MovieModel;
import com.rentalmovie.movie.services.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.rentalmovie.movie.testutils.JwtTokenUtil.generateToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class RentalMovieControllerTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieService movieService;

    private MovieModel generateMovie() {
        final var movieModel = new MovieModel();
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
        return movieService.save(movieModel);
    }

    @Test
    void getAllRentalMoviesTest_HappyPath() throws Exception {
        var movie = generateMovie();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenAnswer( invocation -> ResponseEntity.ok().body(List.of(movie.getMovieId())));

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/movies/rentals").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].movieId").value(movie.getMovieId().toString()));
    }
}