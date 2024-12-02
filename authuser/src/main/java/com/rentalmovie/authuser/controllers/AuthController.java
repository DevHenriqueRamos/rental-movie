package com.rentalmovie.authuser.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rentalmovie.authuser.config.security.JwtProvider;
import com.rentalmovie.authuser.dtos.JwtDTO;
import com.rentalmovie.authuser.dtos.LoginDTO;
import com.rentalmovie.authuser.dtos.UserDTO;
import com.rentalmovie.authuser.enums.RoleType;
import com.rentalmovie.authuser.enums.UserStatus;
import com.rentalmovie.authuser.enums.UserType;
import com.rentalmovie.authuser.models.RoleModel;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.services.RoleService;
import com.rentalmovie.authuser.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.rentalmovie.authuser.utils.ResponseUtils.createMessageResponse;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationProvider authenticationProvider;

    private final JwtProvider jwtProvider;

    public AuthController (UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider ,JwtProvider jwtProvider) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtProvider = jwtProvider;
    }



    @PostMapping("/signup")
    public ResponseEntity<Object> register(
            @RequestBody
            @Validated(UserDTO.UserView.RegisterPost.class)
            @JsonView(UserDTO.UserView.RegisterPost.class)
            UserDTO userDTO
    ) {
        log.debug("POST register userDTO: {}", userDTO.toString());
        if (userService.existsByEmail(userDTO.getEmail())){
            log.warn("Email is already taken email: {}", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createMessageResponse("Email is already taken"));
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserModel userModel = saveNewUser(userDTO);
        log.debug("POST register userId: {}", userModel.getUserId());
        log.info("User registered successfully userId: {}", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    private UserModel saveNewUser(UserDTO userDTO) {
        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_CONSUMER);
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.CONSUMER);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);

        return userService.save(userModel);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtDTO(jwt));
    }
}
