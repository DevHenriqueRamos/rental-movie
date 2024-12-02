package com.rentalmovie.authuser.services.impl;

import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.models.RoleModel;
import com.rentalmovie.authuser.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @InjectMocks
    RoleServiceImpl roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void findByRoleName_HappyPath() {
        when(roleRepository.findByRoleName(any(RoleType.class))).thenReturn(Optional.of(new RoleModel(UUID.randomUUID(), RoleType.ROLE_ADMIN)));

        final var response = roleService.findByRoleName(RoleType.ROLE_ADMIN);
        assertNotNull(response);
        assertEquals(RoleModel.class, response.getClass());
        assertEquals("ROLE_ADMIN", response.getAuthority());

        verify(roleRepository, times(1)).findByRoleName(any(RoleType.class));
    }

}