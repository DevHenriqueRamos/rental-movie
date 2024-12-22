package com.rentalmovie.notification.dtos;

import com.rentalmovie.notification.enums.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationDTO {

    @NotNull
    private NotificationStatus notificationStatus;

}
