package com.rentalmovie.authuser.services.impl;

import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.exceptions.ResourceNotFoundException;
import com.rentalmovie.authuser.models.RoleModel;
import com.rentalmovie.authuser.repositories.RoleRepository;
import com.rentalmovie.authuser.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleModel findByRoleName(RoleType roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Anyone role found with name: " + roleName));
    }
}
