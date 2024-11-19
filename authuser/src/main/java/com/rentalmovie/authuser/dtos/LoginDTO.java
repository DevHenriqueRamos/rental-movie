package com.rentalmovie.authuser.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
