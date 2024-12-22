package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.models.GenreModel;
import com.rentalmovie.movie.repositories.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.rentalmovie.movie.testutils.JwtTokenUtil.generateToken;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreRepository genreRepository;

    private GenreModel createGenreModel(String genreName) {
        final var genreModel = new GenreModel();
        genreModel.setName(genreName);
        genreModel.setDescription("Genre test description");
        genreModel.setCreationDate(LocalDateTime.now());
        return genreRepository.save(genreModel);
    }

    @Test
    void getByIdTest_HappyPath() throws Exception {
        final var genreModel = createGenreModel("Genre test");
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/genres/{genreId}", genreModel.getGenreId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(genreModel.getGenreId().toString()))
                .andExpect(jsonPath("$.name").value(genreModel.getName()));
    }

    @Test
    void getAllTest_HappyPath() throws Exception {
        createGenreModel("Genre test");
        createGenreModel("Genre test 2");

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/genres").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void saveTest_HappyPath() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String genreDTO = """
            {
                "name": "Genre Test",
                "description": "Genre test description"
            }
        """;

        mockMvc.perform(post("/genres").header("Authorization", token)
                .contentType(APPLICATION_JSON)
                .content(genreDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Genre Test"))
                .andExpect(jsonPath("$.description").value("Genre test description"));

        Optional<GenreModel> savedGenre = genreRepository.findByName("Genre Test");
        assertTrue(savedGenre.isPresent());

    }

    @Test
    void updateTest_HappyPath() throws Exception {
        final var genreModel = createGenreModel("Genre test");
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String genreDTO = """
            {
                "name": "Genre test update",
                "description": "Genre test description update"
            }
        """;

        mockMvc.perform(put("/genres/update/{genreId}", genreModel.getGenreId()).header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content(genreDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genreId").value(genreModel.getGenreId().toString()))
                .andExpect(jsonPath("$.name").value("Genre test update"))
                .andExpect(jsonPath("$.description").value("Genre test description update"));
    }

    @Test
    void deleteTest_HappyPath() throws Exception {
        final var genreModel = createGenreModel("Genre test");
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(delete("/genres/delete/{genreId}", genreModel.getGenreId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Genre deleted successfully"));

        Optional<GenreModel> deleteGenre = genreRepository.findById(genreModel.getGenreId());
        assertTrue(deleteGenre.isEmpty());
    }

    @Test
    void genreNotFoundTest() throws Exception {
        final var genreId = UUID.randomUUID();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/genres/{genreId}", genreId).header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Genre not found with id: " + genreId));
    }

    @Test
    void invalidRequestContentTest() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(post("/genres").header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(jsonPath("$.errors.description").value("must not be blank"));
    }

    @Test
    void requestBodyMissingTest() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(post("/genres").header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request body is missing"));
    }

    @Test
    void userWithRoleConsumerCantAccessAdminMethodTest() throws Exception {
        final var genreId = UUID.randomUUID();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_CONSUMER", 60000);

        mockMvc.perform(delete("/genres/delete/{genreId}", genreId)
                        .header("Authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void invalidTokenTest() throws Exception {
        String token = "invalidToken";

        UUID randomUUID = UUID.randomUUID();

        mockMvc.perform(get("/genres/{genreId}", randomUUID)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"));
    }

}