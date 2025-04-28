package com.personal.RealTimeNotification.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
	
	private Long id;
	private Long userId;
    private String title;
    private String message;
    private String status;
    private Instant createdAt;
	@Override
	public String toString() {
		return "NotificationDto [id=" + id + ", userId=" + userId + ", title=" + title + ", message=" + message
				+ ", status=" + status + ", createdAt=" + createdAt + "]";
	}
	
    
}
