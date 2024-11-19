package com.rentalmovie.authuser.services.impl;

import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.models.RoleModel;
import com.rentalmovie.authuser.repositories.RoleRepository;
import com.rentalmovie.authuser.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Optional<RoleModel> findByRoleName(RoleType roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
