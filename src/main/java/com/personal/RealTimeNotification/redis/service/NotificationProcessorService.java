package com.personal.RealTimeNotification.redis.service;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.personal.RealTimeNotification.constants.NotificationStatus;
import com.personal.RealTimeNotification.dto.NotificationDto;
import com.personal.RealTimeNotification.entity.Notification;
import com.personal.RealTimeNotification.entity.User;
import com.personal.RealTimeNotification.exception.UserNotFoundException;
import com.personal.RealTimeNotification.mapper.NotificationMapper;
import com.personal.RealTimeNotification.repository.NotificationRepository;
import com.personal.RealTimeNotification.repository.UserRepository;

import jakarta.annotation.PreDestroy;

@Service
public class NotificationProcessorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProcessorService.class);
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
   // private final BlockingQueue<NotificationDto> queue;
    private final RedisQueueService redisQueueService;
    private final UserRepository userRepository;
    private volatile boolean keepProcessing = true;
    
    public NotificationProcessorService(NotificationRepository notificationRepository,NotificationMapper notificationMapper, BlockingQueue<NotificationDto> queue,RedisQueueService redisQueueService,
    		UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
      //  this.queue = queue;
        this.redisQueueService=redisQueueService;
        this.userRepository=userRepository;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void processNotifications() {
    	 LOGGER.info("Notification processor thread started");
        while (keepProcessing) {
            try {
                NotificationDto dto = redisQueueService.popFromQueue();
                if(dto == null) continue;
                
                LOGGER.info("Processing notification from queue: {}", dto);
                User user = userRepository.findById(dto.getUserId())
                        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + dto.getUserId()));
                Notification notification = notificationMapper.convertDtoToNotificationEntity(dto);
                notification.setId(null); // Optional safety check
                notification.setUser(user);
                notification.setStatus(NotificationStatus.UNREAD);
                notification.setCreatedAt(Instant.now());

                int maxRetries = 3;
                int attempt = 0;
                boolean saved = false;

                while (attempt < maxRetries && !saved) {
                    try {
                        notificationRepository.save(notification);
                        saved = true;
                        LOGGER.info("Notification saved successfully after {} attempt: {}", attempt + 1, dto.getMessage());
                    } catch (Exception e) {
                        attempt++;
                        LOGGER.warn("Save failed, This is our Attempt {}/{}: {}", attempt, maxRetries, e.getMessage());
                        Thread.sleep(2000); 
                    }
                }

                if (!saved) {
                	LOGGER.error("Failed to save after retries. Message: {}", dto.getMessage());
                }

            } catch (InterruptedException e) {
                LOGGER.error("Notification Processing Interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("Notification Processor Gracefully Stopped.");
    }
    
    @PreDestroy
    public void shutdown() {
    	keepProcessing  = false;
        LOGGER.info("Gracefully Stopping Queue Consumer...");
    }
}
