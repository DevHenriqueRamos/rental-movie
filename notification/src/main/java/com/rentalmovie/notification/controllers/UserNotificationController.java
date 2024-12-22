package com.rentalmovie.notification.controllers;

import com.rentalmovie.notification.dtos.NotificationDTO;
import com.rentalmovie.notification.models.NotificationModel;
import com.rentalmovie.notification.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserNotificationController {

    private final NotificationService notificationService;

    public UserNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/user/{userId}/notifications")
    public ResponseEntity<Page<NotificationModel>> getAllNotificationsByUser(
            @PathVariable UUID userId,
            @PageableDefault(sort = "notificationId", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.findAllNotificationsByUser(userId, pageable));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PutMapping("/users/{userId}/notifications/{notificationId}")
    public ResponseEntity<Object> updateNotification(
            @PathVariable UUID userId,
            @PathVariable UUID notificationId,
            @RequestBody @Valid NotificationDTO notificationDTO
    ) {
        NotificationModel notificationModel = notificationService.findByNotificationIdAndUserId(notificationId, userId);

        notificationModel.setNotificationStatus(notificationDTO.getNotificationStatus());
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.saveNotification(notificationModel));
    }
}
