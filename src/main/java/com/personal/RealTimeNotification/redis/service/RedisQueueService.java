package com.personal.RealTimeNotification.redis.service;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.personal.RealTimeNotification.dto.NotificationDto;

@Service
public class RedisQueueService {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(RedisQueueService.class);
	 private final String QUEUE_KEY = "notification_queue";
	 private RedisTemplate<String, Object> redisTemplate;
	 
	 public RedisQueueService(RedisTemplate<String, Object> redisTemplate) {
		 this.redisTemplate=redisTemplate;
	 }
	 
	 public void pushInQueue(NotificationDto notificationDto) {
		 redisTemplate.opsForList().leftPush(QUEUE_KEY, notificationDto);
		 LOGGER.info("Pushed to Redis queue: {}", notificationDto.getMessage());
	 }
	 
	 public NotificationDto popFromQueue() {
		Object resObject = redisTemplate.opsForList().rightPop(QUEUE_KEY,Duration.ofSeconds(5));
		if (resObject instanceof NotificationDto notificationDto) {
			return notificationDto;
		}
		return null;
	 }
	 
	 public NotificationDto pollFromQueue() {
		    Object data = redisTemplate.opsForList().rightPop(QUEUE_KEY);
		    if (data instanceof NotificationDto notificationDto) {
		        return notificationDto;
		    }
		    return null;
		}
	 
	 public boolean isQueueEmpty() {
		    Long size = redisTemplate.opsForList().size(QUEUE_KEY);
		    return size == null || size == 0;
		}

}
