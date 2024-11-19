package com.rentalmovie.authuser.services;

import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.models.RoleModel;

import java.util.Optional;

public interface RoleService {

    Optional<RoleModel> findByRoleName(RoleType roleName);
}
