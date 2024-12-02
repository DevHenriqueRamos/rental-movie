package com.rentalmovie.authuser.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rentalmovie.authuser.config.security.AuthenticationCurrentUserService;
import com.rentalmovie.authuser.dtos.UserDTO;
import com.rentalmovie.authuser.models.UserModel;
import com.rentalmovie.authuser.services.UserService;
import com.rentalmovie.authuser.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
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
import java.util.Map;
import java.util.UUID;

import static com.rentalmovie.authuser.utils.ResponseUtils.createMessageResponse;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<UserModel>> getAllUsers(
            SpecificationTemplate.UserSpecification specification,
            @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(specification, pageable);
        userModelPage.stream()
                .forEach(user -> user.add(linkTo(methodOn(UserController.class).getUserById(user.getUserId())).withSelfRel()));
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userId));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/me")
    public ResponseEntity<UserModel> getAuthenticatedUser() {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(currentUserId));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteById() {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("DELETE deleteById userId: {}", currentUserId);
        userService.delete(currentUserId);
        log.info("User deleted userId: {}", currentUserId);
        return ResponseEntity.status(HttpStatus.OK).body(createMessageResponse("User deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PutMapping("/update")
    public ResponseEntity<UserModel> updateUser(
            @RequestBody @Validated(UserDTO.UserView.UserPut.class) @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDTO)
    {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("PUT updateUser userId: {}", currentUserId);
        UserModel userModel = userService.findById(currentUserId);
        userModel.setFullName(userDTO.getFullName());
        userModel.setCpf(userDTO.getCpf());
        userModel.setPhoneNumber(userDTO.getPhoneNumber());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userModel = userService.save(userModel);
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
        UserModel userModel = userService.findById(currentUserId);

        if (!passwordEncoder.matches(userDTO.getOldPassword(), userModel.getPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createMessageResponse("Current password does not match"));
        }

        userModel.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("PUT updatePassword userId: {}", currentUserId);
        log.info("User password updated successfully");

        return ResponseEntity.status(HttpStatus.OK).body(createMessageResponse("Password updated successfully"));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PutMapping("/update/image")
    public ResponseEntity<Object> updateImage(
            @RequestBody @Validated(UserDTO.UserView.ImagePut.class) @JsonView(UserDTO.UserView.ImagePut.class) UserDTO userDTO) {

        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.debug("PUT updateImage userDTO: {}", userDTO.toString());
        UserModel userModel = userService.findById(currentUserId);
        userModel.setImageUrl(userDTO.getImageUrl());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("PUT updateImage userModel: {}", userModel.toString());
        log.info("User Image updated successfully");

        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }
}
