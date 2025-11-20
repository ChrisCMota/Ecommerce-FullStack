package com.foodapp.FoodApp.email_notification.repository;

import com.foodapp.FoodApp.email_notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
