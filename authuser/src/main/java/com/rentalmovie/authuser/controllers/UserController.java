package com.rentalmovie.authuser.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rentalmovie.authuser.config.security.AuthenticationCurrentUserService;
import com.rentalmovie.authuser.dtos.UserDTO;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.services.UserService;
import com.rentalmovie.authuser.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationCurrentUserService authenticationCurrentUserService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<UserModel>> getAllUsers(
            SpecificationTemplate.UserSpecification specification,
            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(specification, pageable);
        if (userModelPage.hasContent()) {
            for (UserModel user : userModelPage.getContent()) {
                user.add(linkTo(methodOn(UserController.class).getUserById(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID userId) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
        }
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/me")
    public ResponseEntity<Object> getAuthenticatedUser() {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        Optional<UserModel> userModelOptional = userService.findById(currentUserId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
        }
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteById() {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("DELETE deleteById userId: {}", currentUserId);
        Optional<UserModel> userModelOptional = userService.findById(currentUserId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
        }
        userService.delete(currentUserId);
        log.info("User deleted userId: {}", currentUserId);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(
            @RequestBody @Validated(UserDTO.UserView.UserPut.class) @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDTO)
    {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("PUT updateUser userId: {}", currentUserId);
        Optional<UserModel> userModelOptional = userService.findById(currentUserId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
        }
        var userModel = userModelOptional.get();
        userModel.setFullName(userDTO.getFullName());
        userModel.setCpf(userDTO.getCpf());
        userModel.setPhoneNumber(userDTO.getPhoneNumber());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("PUT updateUser userId: {}", currentUserId);
        log.info("User updated successfully");

        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PutMapping("/update/password")
    public ResponseEntity<Object> updatePassword(
            @RequestBody @Validated(UserDTO.UserView.PasswordPut.class) @JsonView(UserDTO.UserView.PasswordPut.class) UserDTO userDTO) {

        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("PUT updatePassword userId: {}", currentUserId);
        Optional<UserModel> userModelOptional = userService.findById(currentUserId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
        }

        if (!passwordEncoder.matches(userDTO.getOldPassword(), userModelOptional.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Current password does not match");
        }
        var userModel = userModelOptional.get();
        userModel.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("PUT updatePassword userId: {}", currentUserId);
        log.info("User password updated successfully");

        return ResponseEntity.status(HttpStatus.OK).body("Passoword updated successfully");
    }

    @Transactional
    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PutMapping("/update/image")
    public ResponseEntity<Object> updateImage(
            @RequestBody @Validated(UserDTO.UserView.ImagePut.class) @JsonView(UserDTO.UserView.ImagePut.class) UserDTO userDTO) {

        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("PUT updateImage userDTO: {}", userDTO.toString());
        Optional<UserModel> userModelOptional = userService.findById(currentUserId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
        }

        var userModel = userModelOptional.get();
        userModel.setImageUrl(userDTO.getImageUrl());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("PUT updateImage userModel: {}", userModel.toString());
        log.info("User Image updated successfully");

        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }
}
