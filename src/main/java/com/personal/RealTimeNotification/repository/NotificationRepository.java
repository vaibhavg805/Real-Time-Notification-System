package com.personal.RealTimeNotification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.RealTimeNotification.constants.NotificationStatus;
import com.personal.RealTimeNotification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
	public List<Notification> findByUserId(Long userId);
	public List<Notification> findByUserIdAndStatus(Long userId,NotificationStatus status);
	Optional<Notification> findById(Long notificationId);
}
