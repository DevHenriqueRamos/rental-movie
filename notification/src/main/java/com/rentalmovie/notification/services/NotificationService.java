package com.rentalmovie.notification.services;

import com.rentalmovie.notification.models.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    Page<NotificationModel> findAllNotificationsByUser(UUID userId, Pageable pageable);

    NotificationModel findByNotificationIdAndUserId(UUID notificationId, UUID userId);

    NotificationModel saveNotification(NotificationModel notificationModel);
}
