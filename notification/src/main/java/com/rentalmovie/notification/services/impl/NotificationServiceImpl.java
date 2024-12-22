package com.rentalmovie.notification.services.impl;

import com.rentalmovie.notification.enums.NotificationStatus;
import com.rentalmovie.notification.exceptions.ResourceNotFoundException;
import com.rentalmovie.notification.models.NotificationModel;
import com.rentalmovie.notification.repositories.NotificationRepository;
import com.rentalmovie.notification.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Page<NotificationModel> findAllNotificationsByUser(UUID userId, Pageable pageable) {
        return notificationRepository.findAllByUserIdAndNotificationStatus(userId, NotificationStatus.CREATED, pageable);
    }

    @Override
    public NotificationModel findByNotificationIdAndUserId(UUID notificationId, UUID userId) {
        return notificationRepository.findByNotificationIdAndUserId(userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found!"));
    }

    @Override
    public NotificationModel saveNotification(NotificationModel notificationModel) {
        return notificationRepository.save(notificationModel);
    }
}
