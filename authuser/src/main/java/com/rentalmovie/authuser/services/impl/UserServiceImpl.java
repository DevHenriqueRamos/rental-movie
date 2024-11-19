package com.rentalmovie.authuser.services.impl;

import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.repositories.UserRepository;
import com.rentalmovie.authuser.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void delete(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void save(UserModel userModel) {
        userRepository.save(userModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<UserModel> findAll(Specification<UserModel> specification, Pageable pageable) {
        return userRepository.findAll(specification, pageable);
    }
}
