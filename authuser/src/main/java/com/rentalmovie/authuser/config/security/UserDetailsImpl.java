package com.rentalmovie.authuser.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentalmovie.authuser.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private UUID userId;
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String username;


    public static UserDetailsImpl build(UserModel userModel) {
        List<GrantedAuthority> authorities = userModel.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                userModel.getUserId(),
                userModel.getFullName(),
                userModel.getEmail(),
                userModel.getPassword(),
                authorities,
                null
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
