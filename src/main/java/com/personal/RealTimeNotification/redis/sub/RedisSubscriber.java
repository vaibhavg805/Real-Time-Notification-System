package com.personal.RealTimeNotification.redis.sub;

import com.personal.RealTimeNotification.constants.NotificationStatus;
import com.personal.RealTimeNotification.entity.Notification;
import com.personal.RealTimeNotification.entity.User;
import com.personal.RealTimeNotification.repository.NotificationRepository;
import com.personal.RealTimeNotification.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.RealTimeNotification.dto.NotificationDto;

import java.time.Instant;
//import com.personal.RealTimeNotification.redis.service.NotificationQueueService;

@Component
public class RedisSubscriber implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);
	
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	//private NotificationQueueService notificationQueueService;
	public RedisSubscriber(@Qualifier("redisObjectMapper") ObjectMapper objectMapper,
						   UserRepository userRepository,
						   NotificationRepository notificationRepository) {
		this.userRepository=userRepository;
		this.notificationRepository = notificationRepository;
		this.objectMapper=objectMapper;
	//	this.notificationQueueService=notificationQueueService;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String json = new String(message.getBody());
			NotificationDto notificationDto = objectMapper.readValue(json, NotificationDto.class);

			// Convert DTO → Entity
			Notification notification = new Notification();
			notification.setMessage(notificationDto.getMessage());
			notification.setStatus(NotificationStatus.UNREAD);
			notification.setCreatedAt(Instant.now());
			notification.setTitle(notificationDto.getTitle());

			// need user as well (fetch by ID)
			User user = userRepository.findById(notificationDto.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			notification.setUser(user);

			// SAVE DIRECTLY
			notificationRepository.save(notification);

		} catch (Exception e) {
			LOGGER.error("Failed to process notification: {}", e.getMessage());
		}
	}

}
