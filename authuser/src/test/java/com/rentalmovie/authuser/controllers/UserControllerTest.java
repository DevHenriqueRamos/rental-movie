package com.rentalmovie.authuser.controllers;

import com.rentalmovie.authuser.config.security.JwtProvider;
import com.rentalmovie.authuser.config.security.UserDetailsServiceImpl;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.rentalmovie.authuser.creator.CreatorUtils.generateMock;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private RoleModel roleAdmin;

    @BeforeEach
    public void setUp() {
        RoleModel adminRole = new RoleModel(UUID.randomUUID(), RoleType.ROLE_ADMIN);
        RoleModel consumerRole = new RoleModel(UUID.randomUUID(), RoleType.ROLE_CONSUMER);
        roleRepository.save(adminRole);
        roleRepository.save(consumerRole);
        roleAdmin = roleRepository.findByRoleName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
    }

    private String generateJwtToken(UUID userId) {
        UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(authentication);
    }

    private UserModel generateUserModel() {
        final var entity = generateMock(UserModel.class);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.getRoles().clear();
        entity.getRoles().add(roleAdmin);
        return userRepository.save(entity);
    }

    @Test
    void getUserByIdTest_HappyPath() throws Exception {
        final var userModel = generateUserModel();

        String token = generateJwtToken(userModel.getUserId());

        mockMvc.perform(get("/users/{userId}", userModel.getUserId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userModel.getUserId().toString()))
                .andExpect(jsonPath("$.email").value(userModel.getEmail()))
                .andExpect(jsonPath("$.cpf").value(userModel.getCpf()));
    }

    @Test
    void getAllUsersTest_HappyPath() throws Exception {
        final var userModel1 = generateUserModel();
        generateUserModel();

        String token = generateJwtToken(userModel1.getUserId());

        mockMvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));

    }

    @Test
    void getAuthenticatedUserTest_HappyPath() throws Exception {
        final var userModel = generateUserModel();

        String token = generateJwtToken(userModel.getUserId());

        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userModel.getUserId().toString()));
    }

    @Test
    void deleteByIdTest_HappyPath() throws Exception {
        final var userModel = generateUserModel();

        String token = generateJwtToken(userModel.getUserId());

        mockMvc.perform(delete("/users/delete").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        Optional<UserModel> deleteUser = userRepository.findById(userModel.getUserId());
        assertTrue(deleteUser.isEmpty());
    }

    @Test
    void updateUserTest_HappyPath() throws Exception {
        final var userModel = generateUserModel();
        String token = generateJwtToken(userModel.getUserId());

        String userDTO = """
            {
                "phoneNumber": "01199999999",
                "fullName": "John Doe",
                "cpf": "123.456.789-09"
            }
        """;

        mockMvc.perform(put("/users/update")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userModel.getUserId().toString()))
                .andExpect(jsonPath("$.phoneNumber").value("01199999999"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));
    }

    @Test
    void updateUserTest_InvalidCpf() throws Exception {
        final var userModel = generateUserModel();
        String token = generateJwtToken(userModel.getUserId());

        String userDTO = """
            {
                "phoneNumber": "01199999999",
                "fullName": "John Doe",
                "cpf": "111.111.111-11"
            }
        """;

        mockMvc.perform(put("/users/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request content"))
                .andExpect(jsonPath("$.errors.cpf").value("Invalid cpf"));
    }

    @Test
    void updatePasswordTest_HappyPath() throws Exception {
        final var userModel = generateMock(UserModel.class);
        String userPassword = userModel.getPassword();

        userModel.setPassword(passwordEncoder.encode(userPassword));
        userModel.getRoles().clear();
        userModel.getRoles().add(roleAdmin);
        final var savedUser = userRepository.save(userModel);

        String token = generateJwtToken(savedUser.getUserId());

        String userDTO = """
            {
                "password": "123456",
                "oldPassword": "%s"
            }
        """.formatted(userPassword);

        mockMvc.perform(put("/users/update/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));

        UserModel userWithPasswordUpdated = userRepository.findById(savedUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        assertTrue(passwordEncoder.matches("123456", userWithPasswordUpdated.getPassword()));
    }

    @Test
    void updatePasswordTest_MissedCurrentPassword() throws Exception {
        final var userModel = generateUserModel();
        String token = generateJwtToken(userModel.getUserId());

        String userDTO = """
            {
                "password": "123456",
                "oldPassword": "missedPassword"
            }
        """;

        mockMvc.perform(put("/users/update/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Current password does not match"));
    }

    @Test
    void updateImageTest_HappyPath() throws Exception {
        final var userModel = generateUserModel();
        String token = generateJwtToken(userModel.getUserId());

        String userDTO = """
            {
                "imageUrl": "new user image url"
            }
        """;

        mockMvc.perform(put("/users/update/image")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userModel.getUserId().toString()))
                .andExpect(jsonPath("$.imageUrl").value("new user image url"));
    }

    @Test
    void userNotFoundTest() throws Exception {
        final var userModel = generateUserModel();

        String token = generateJwtToken(userModel.getUserId());

        UUID randomUUID = UUID.randomUUID();

        mockMvc.perform(get("/users/{userId}", randomUUID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Any user found with userId: " + randomUUID));
    }

    @Test
    void userWithRoleConsumerCantAccessAdminMethodTest() throws Exception {
        final var roleConsumer = roleRepository.findByRoleName(RoleType.ROLE_CONSUMER)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        final var entity = generateMock(UserModel.class);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.getRoles().clear();
        entity.getRoles().add(roleConsumer);
        final var consumerUser = userRepository.save(entity);

        String token = generateJwtToken(consumerUser.getUserId());

        mockMvc.perform(get("/users/{userId}", consumerUser.getUserId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void invalidTokenTest() throws Exception {
        String token = "invalidToken";

        UUID randomUUID = UUID.randomUUID();

        mockMvc.perform(get("/users/{userId}", randomUUID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"));
    }
}