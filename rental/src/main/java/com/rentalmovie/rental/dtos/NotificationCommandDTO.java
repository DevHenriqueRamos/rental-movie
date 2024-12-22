package com.rentalmovie.rental.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class NotificationCommandDTO {

    private UUID userId;
    private String title;
    private String message;

}
