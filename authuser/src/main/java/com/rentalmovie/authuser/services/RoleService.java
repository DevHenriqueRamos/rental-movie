package com.rentalmovie.authuser.services;

import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.models.RoleModel;

public interface RoleService {

    RoleModel findByRoleName(RoleType roleName);
}
