package com.personal.RealTimeNotification.service;

import java.util.List;

import com.personal.RealTimeNotification.dto.NotificationDto;
import com.personal.RealTimeNotification.dto.ProcessingResponseDto;

public interface NotificationService {
	
    ProcessingResponseDto<String> createNotification(Long userId, NotificationDto dto);
    List<NotificationDto> getAllNotifications(Long userId);
    List<NotificationDto> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
    String deleteNotification(Long notificationId);
    NotificationDto publishNotification(Long userId, NotificationDto dto);

}
