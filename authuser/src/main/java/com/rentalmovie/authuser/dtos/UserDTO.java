package com.rentalmovie.authuser.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

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

    @NotBlank(groups = UserView.RegisterPost.class, message = "Email is required")
    @Email(groups = UserView.RegisterPost.class, message = "Invalid email format")
    @JsonView(UserView.RegisterPost.class)
    private String email;

    @NotBlank(groups = {UserView.RegisterPost.class, UserView.PasswordPut.class}, message = "Password is required")
    @Size(min = 6, max = 20, groups = {UserView.RegisterPost.class, UserView.PasswordPut.class}, message = "Password can't be less than 6 and over 20")
    @JsonView({UserView.RegisterPost.class, UserView.PasswordPut.class})
    private String password;

    @NotBlank(groups = UserView.PasswordPut.class, message = "Current password is required")
    @Size(min = 6, max = 20, groups = UserView.PasswordPut.class, message = "Password can't be less than 6 and over 20")
    @JsonView(UserView.PasswordPut.class)
    private String oldPassword;

    @JsonView({UserView.RegisterPost.class, UserView.UserPut.class})
    private String phoneNumber;

    @NotBlank(groups = {UserView.RegisterPost.class, UserView.UserPut.class}, message = "full name is required")
    @JsonView({UserView.RegisterPost.class, UserView.UserPut.class})
    private String fullName;

    @CPF(groups = {UserView.RegisterPost.class, UserView.UserPut.class}, message = "Invalid cpf")
    @NotBlank(groups = {UserView.RegisterPost.class, UserView.UserPut.class}, message = "cpf is required")
    @JsonView({UserView.RegisterPost.class, UserView.UserPut.class})
    private String cpf;

    @NotBlank(groups = UserView.ImagePut.class, message = "image url is required")
    @JsonView(UserView.ImagePut.class)
    private String imageUrl;
}
