package com.rentalmovie.authuser.services;

import com.rentalmovie.authuser.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<UserModel> findAll();

    Optional<UserModel> findById(UUID userId);

    void delete(UUID userId);

    void save(UserModel userModel);

    boolean existsByEmail(String email);

    Page<UserModel> findAll(Specification<UserModel> specification, Pageable pageable);
}
