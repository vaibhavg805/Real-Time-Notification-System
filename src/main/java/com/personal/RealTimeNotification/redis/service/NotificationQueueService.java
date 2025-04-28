package com.personal.RealTimeNotification.redis.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.personal.RealTimeNotification.dto.NotificationDto;

@Service
public class NotificationQueueService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationQueueService.class);
//	private final BlockingQueue<NotificationDto> notificationQueue = new LinkedBlockingQueue<>();
	private final RedisQueueService redisQueueService;
	
	public NotificationQueueService(RedisQueueService redisQueueService) {
		this.redisQueueService=redisQueueService;
	}
	
	public void enqueue(NotificationDto dto) {
	//	notificationQueue.offer(dto);
		redisQueueService.pushInQueue(dto);
		LOGGER.info("Notification added to processing queue: {}", dto);
	}
	
	  public NotificationDto dequeue() {
	        return redisQueueService.pollFromQueue();
	    }

	    public boolean isEmpty() {
	        return redisQueueService.isQueueEmpty();
	    }
}
