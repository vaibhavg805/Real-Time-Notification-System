package com.personal.RealTimeNotification.redis.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.personal.RealTimeNotification.dto.NotificationDto;

@Configuration
public class QueueConfig {

	 @Bean
	  BlockingQueue<NotificationDto> notificationQueue() {
	        return new LinkedBlockingQueue<>();
	    }
}
