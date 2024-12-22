package com.rentalmovie.movie.controllers;

import com.rentalmovie.movie.models.ProductionStudioModel;
import com.rentalmovie.movie.repositories.ProductionStudioRepository;
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
class ProductionStudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProductionStudioRepository productionStudioRepository;

    private ProductionStudioModel createProductionStudioModel(String productionStudioName) {
        final var productionStudioModel = new ProductionStudioModel();
        productionStudioModel.setName(productionStudioName);
        productionStudioModel.setDescription("Production studio test description");
        productionStudioModel.setCreationDate(LocalDateTime.now());
        return productionStudioRepository.save(productionStudioModel);
    }

    @Test
    void getByIdTest_HappyPath() throws Exception {
        final var productionStudioModel = createProductionStudioModel("Production studio test");
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/production-studios/{productionStudioId}", productionStudioModel.getProductionStudioId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productionStudioId").value(productionStudioModel.getProductionStudioId().toString()))
                .andExpect(jsonPath("$.name").value(productionStudioModel.getName()));
    }

    @Test
    void getAllTest_HappyPath() throws Exception {
        createProductionStudioModel("Production studio test");
        createProductionStudioModel("Production studio test 2");

        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/production-studios").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void saveTest_HappyPath() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String productionStudioDTO = """
            {
                "name": "Production studio test",
                "description": "Production studio test description"
            }
        """;

        mockMvc.perform(post("/production-studios").header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content(productionStudioDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Production studio test"))
                .andExpect(jsonPath("$.description").value("Production studio test description"));

        Optional<ProductionStudioModel> savedProductionStudio = productionStudioRepository.findByName("Production studio test");
        assertTrue(savedProductionStudio.isPresent());

    }

    @Test
    void updateTest_HappyPath() throws Exception {
        final var productionStudioModel = createProductionStudioModel("Production studio test");
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        String productionStudioDTO = """
            {
                "name": "Production studio test update",
                "description": "Production studio test description update"
            }
        """;

        mockMvc.perform(put("/production-studios/update/{productionStudioId}", productionStudioModel.getProductionStudioId())
                        .header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content(productionStudioDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productionStudioId").value(productionStudioModel.getProductionStudioId().toString()))
                .andExpect(jsonPath("$.name").value("Production studio test update"))
                .andExpect(jsonPath("$.description").value("Production studio test description update"));
    }

    @Test
    void deleteTest_HappyPath() throws Exception {
        final var productionStudioModel = createProductionStudioModel("Production studio test");
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(delete("/production-studios/delete/{productionStudioId}", productionStudioModel.getProductionStudioId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Production studio deleted successfully"));

        Optional<ProductionStudioModel> deleteProductionStudio = productionStudioRepository.findById(productionStudioModel.getProductionStudioId());
        assertTrue(deleteProductionStudio.isEmpty());
    }

    @Test
    void productionStudioNotFoundTest() throws Exception {
        final var productionStudioId = UUID.randomUUID();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(get("/production-studios/{productionStudioId}", productionStudioId).header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Production studio not found with id: " + productionStudioId));
    }

    @Test
    void invalidRequestContentTest() throws Exception {
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_ADMIN", 60000);

        mockMvc.perform(post("/production-studios").header("Authorization", token)
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

        mockMvc.perform(post("/production-studios").header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request body is missing"));
    }

    @Test
    void userWithRoleConsumerCantAccessAdminMethodTest() throws Exception {
        final var productionStudioId = UUID.randomUUID();
        String token = "Bearer " + generateToken(UUID.randomUUID().toString(), "ROLE_CONSUMER", 60000);

        mockMvc.perform(delete("/production-studios/delete/{productionStudioId}", productionStudioId)
                        .header("Authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void invalidTokenTest() throws Exception {
        String token = "invalidToken";

        UUID randomUUID = UUID.randomUUID();

        mockMvc.perform(get("/production-studios/{productionStudioId}", randomUUID)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"));
    }
}