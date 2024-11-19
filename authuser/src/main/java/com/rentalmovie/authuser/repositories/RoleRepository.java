package com.rentalmovie.authuser.repositories;

import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleModel, UUID> {
    Optional<RoleModel> findByRoleName(RoleType name);
}
