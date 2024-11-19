package com.rentalmovie.authuser.config.security;

import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return UserDetailsImpl.build(userModel);
    }

    public UserDetails loadUserByUserId(UUID userId) throws AuthenticationCredentialsNotFoundException {
        UserModel userModel = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return UserDetailsImpl.build(userModel);
    }
}
