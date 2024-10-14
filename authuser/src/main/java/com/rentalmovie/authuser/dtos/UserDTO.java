package com.rentalmovie.authuser.dtos;

import com.rentalmovie.authuser.validations.CpfConstraint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    public interface UserView {
        public static interface RegisterPost {}
        public static interface UserPut {}
        public static interface PasswordPut {}
        public static interface ImagePut {}
    }

    private UUID userId;

    @NotBlank(groups = UserView.RegisterPost.class)
    @Email(groups = UserView.RegisterPost.class)
    @JsonView(UserView.RegisterPost.class)
    private String email;

    @NotBlank(groups = {UserView.RegisterPost.class, UserView.PasswordPut.class})
    @Size(min = 6, max = 20, groups = {UserView.RegisterPost.class, UserView.PasswordPut.class})
    @JsonView({UserView.RegisterPost.class, UserView.PasswordPut.class})
    private String password;

    @NotBlank(groups = UserView.PasswordPut.class)
    @Size(min = 6, max = 20, groups = UserView.PasswordPut.class)
    @JsonView(UserView.PasswordPut.class)
    private String oldPassword;

    @JsonView({UserView.RegisterPost.class, UserView.UserPut.class})
    private String phoneNumber;

    @NotBlank(groups = {UserView.RegisterPost.class, UserView.UserPut.class})
    @JsonView({UserView.RegisterPost.class, UserView.UserPut.class})
    private String fullName;

    @CpfConstraint(groups = {UserView.RegisterPost.class, UserView.UserPut.class})
    @JsonView({UserView.RegisterPost.class, UserView.UserPut.class})
    private String cpf;

    @NotBlank(groups = UserView.ImagePut.class)
    @JsonView(UserView.ImagePut.class)
    private String imageUrl;
}
