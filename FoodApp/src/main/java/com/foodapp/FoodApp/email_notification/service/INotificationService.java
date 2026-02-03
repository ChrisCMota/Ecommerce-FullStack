package com.foodapp.FoodApp.email_notification.service;

import com.foodapp.FoodApp.email_notification.dtos.NotificationDTO;

public interface INotificationService {
    void sendEmail(NotificationDTO notificationDTO);
}
