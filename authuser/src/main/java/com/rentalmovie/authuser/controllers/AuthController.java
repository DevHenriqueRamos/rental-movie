package com.rentalmovie.authuser.controllers;

import com.rentalmovie.authuser.dtos.UserDTO;
import com.rentalmovie.authuser.enums.UserStatus;
import com.rentalmovie.authuser.enums.UserType;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken");
        }
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDTO, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.CONSUMER);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("POST register userModel: {}", userModel.toString());
        log.info("User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }
}
