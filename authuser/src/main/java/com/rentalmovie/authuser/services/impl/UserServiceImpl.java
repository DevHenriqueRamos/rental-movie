package com.rentalmovie.authuser.services.impl;

import com.rentalmovie.authuser.exceptions.ResourceNotFoundException;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.repositories.UserRepository;
import com.rentalmovie.authuser.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Any user found with userId: " + userId));
    }

    @Override
    public void delete(UUID userId) {
        findById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
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
