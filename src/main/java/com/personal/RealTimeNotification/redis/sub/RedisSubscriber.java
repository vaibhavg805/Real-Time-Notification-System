package com.personal.RealTimeNotification.redis.sub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.RealTimeNotification.dto.NotificationDto;
import com.personal.RealTimeNotification.redis.service.NotificationQueueService;

@Component
public class RedisSubscriber implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);
	
	private final ObjectMapper objectMapper;
	private NotificationQueueService notificationQueueService;
	public RedisSubscriber(@Qualifier("redisObjectMapper") ObjectMapper objectMapper,NotificationQueueService notificationQueueService) {
		this.objectMapper=objectMapper;
		this.notificationQueueService=notificationQueueService;
	}
	

	@Override
	public void onMessage(Message message, byte[] pattern) {
		 try {
	            String json = new String(message.getBody());
	            NotificationDto notification = objectMapper.readValue(json, NotificationDto.class);
	            
	            // Simulate handing over for processing
	            notificationQueueService.enqueue(notification);
	        } catch (Exception e) {
	        	 LOGGER.info("Failed to deserialize message: {}",e.getMessage());
	        }
	}

}
