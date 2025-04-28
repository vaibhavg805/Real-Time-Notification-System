package com.personal.RealTimeNotification.redis.pub.implementation;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.personal.RealTimeNotification.redis.pub.interf.RedisPublisher;

@Component
public class RedisPublisherImpl implements RedisPublisher {
	
	private final RedisTemplate<String, Object> redisTemplate;
	public RedisPublisherImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate=redisTemplate;
	}

	@Override
	public void publish(String channel, Object message) {
		redisTemplate.convertAndSend(channel, message);	
	}

}
