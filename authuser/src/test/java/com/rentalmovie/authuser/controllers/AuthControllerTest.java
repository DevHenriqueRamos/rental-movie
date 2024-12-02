package com.rentalmovie.authuser.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalmovie.authuser.dtos.LoginDTO;
import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.models.RoleModel;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.repositories.RoleRepository;
import com.rentalmovie.authuser.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.rentalmovie.authuser.creator.CreatorUtils.generateMock;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        RoleModel adminRole = new RoleModel(UUID.randomUUID(), RoleType.ROLE_ADMIN);
        RoleModel consumerRole = new RoleModel(UUID.randomUUID(), RoleType.ROLE_CONSUMER);
        roleRepository.save(adminRole);
        roleRepository.save(consumerRole);
    }

    private void generateUserModel() {
        RoleModel roleAdmin = roleRepository.findByRoleName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        final var entity = generateMock(UserModel.class);
        entity.setEmail("test@test.com");
        entity.setPassword(passwordEncoder.encode("password123"));
        entity.getRoles().clear();
        entity.getRoles().add(roleAdmin);
        userRepository.save(entity);
    }

    @Test
    void registerTest_HappyPath() throws Exception {

        String userDTO = """
            {
                "email": "test@test.com",
                "password": "password123",
                "phoneNumber": "0119999999",
                "fullName": "John Doe",
                "cpf": "123.456.789-09"
            }
        """;

        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0119999999"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));

        Optional<UserModel> savedUser = userRepository.findByEmail("test@test.com");
        assertTrue(savedUser.isPresent());
    }

    @Test
    void registerTest_EmailAlreadyTaken() throws Exception {
        generateUserModel();

        String userDTO = """
            {
                "email": "test@test.com",
                "password": "password123",
                "phoneNumber": "0119999999",
                "fullName": "John Doe",
                "cpf": "123.456.789-09"
            }
        """;

        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already taken"));
    }

    @Test
    void registerTest_InvalidEmailFormat() throws Exception {

        String userDTO = """
            {
                "email": "invalid email format",
                "password": "password123",
                "phoneNumber": "0119999999",
                "fullName": "John Doe",
                "cpf": "123.456.789-09"
            }
        """;

        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));
    }

    @Test
    void registerTest_InvalidCpf() throws Exception {

        String userDTO = """
            {
                "email": "test@test.com",
                "password": "password123",
                "phoneNumber": "0119999999",
                "fullName": "John Doe",
                "cpf": "111.111.111-11"
            }
        """;

        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.cpf").value("Invalid cpf"));
    }

    @Test
    void loginTest_HappyPath() throws Exception {
        generateUserModel();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@test.com");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));

    }

    @Test
    void loginTest_WrongCredentials() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("wrongemail@test.com");
        loginDTO.setPassword("wrongpassword123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }
}