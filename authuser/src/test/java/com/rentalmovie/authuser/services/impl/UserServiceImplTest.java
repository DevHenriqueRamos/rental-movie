package com.rentalmovie.authuser.services.impl;

import com.rentalmovie.authuser.exceptions.ResourceNotFoundException;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Test
    void findByIdTest_HappyPath() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new UserModel()));

        final var response = userService.findById(UUID.randomUUID());

        assertNotNull(response);
        assertEquals(UserModel.class, response.getClass());

        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findByIdTest_NotFoundAnyUserWithId() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UUID userId = UUID.randomUUID();

        try {
            userService.findById(userId);
        } catch (Exception e) {
            assertEquals(ResourceNotFoundException.class, e.getClass());
            assertEquals("Any user found with userId: " + userId, e.getMessage());
        }

        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findAllTest_HappyPath() {
        Page<UserModel> page = new PageImpl<>(List.of(new UserModel(), new UserModel()));
        Pageable pageable = PageRequest.of(0, 10);
        Specification<UserModel> specification = (root, query, builder) -> null;

        when(userRepository.findAll(specification, pageable)).thenReturn(page);

        final var response = userService.findAll(specification, pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(UserModel.class, response.getContent().getFirst().getClass());

        verify(userRepository, times(1)).findAll(specification, pageable);
    }

    @Test
    void saveTest_HappyPath() {
        UserModel userModel = new UserModel();
        userModel.setEmail("test@test.com");
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        final var response = userService.save(userModel);
        assertNotNull(response);
        assertEquals(UserModel.class, response.getClass());
        assertEquals(userModel.getEmail(), response.getEmail());

        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void deleteTest_HappyPath() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new UserModel()));
        doNothing().when(userRepository).deleteById(any(UUID.class));

        userService.delete(UUID.randomUUID());

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void existsByEmailTest_HappyPath() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        boolean existsEmail = userService.existsByEmail(anyString());
        assertTrue(existsEmail);

        verify(userRepository, times(1)).existsByEmail(anyString());
    }

    @Test
    void findAllWithoutParams_HappyPath() {
        when(userRepository.findAll()).thenReturn(List.of(new UserModel(), new UserModel()));

        final var response = userService.findAll();
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(UserModel.class, response.getFirst().getClass());
    }

}