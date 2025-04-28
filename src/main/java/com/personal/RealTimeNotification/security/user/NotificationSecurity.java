package com.personal.RealTimeNotification.security.user;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.personal.RealTimeNotification.repository.NotificationRepository;

@Component("notificationSecurity")
public class NotificationSecurity {

    private NotificationRepository notificationRepository;
    public NotificationSecurity(NotificationRepository notificationRepository) {
    	this.notificationRepository=notificationRepository;
    }

    public boolean isOwnerOrAdmin(Long notificationId, Authentication authentication) {
        // Check if user is ADMIN
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // Else check if notification belongs to user
        Long loggedInUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        return notificationRepository.findById(notificationId)
                .map(notification -> notification.getUser().getId().equals(loggedInUserId))
                .orElse(false);
    }
}

