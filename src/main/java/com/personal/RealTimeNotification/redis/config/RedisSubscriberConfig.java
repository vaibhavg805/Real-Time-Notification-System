package com.personal.RealTimeNotification.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.personal.RealTimeNotification.redis.channel.RedisChannels;
import com.personal.RealTimeNotification.redis.sub.CommonEventSubscriber;
import com.personal.RealTimeNotification.redis.sub.RedisSubscriber;

@Configuration
public class RedisSubscriberConfig {

	@Bean
	RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, RedisSubscriber redisSubscriber, CommonEventSubscriber commonEventSubscriber) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(new MessageListenerAdapter(redisSubscriber), new PatternTopic(RedisChannels.NOTIFICATION_CHANNEL));
		
		container.addMessageListener(new MessageListenerAdapter(commonEventSubscriber), new PatternTopic(RedisChannels.COMMON_EVENT_CHANNEL));
		
		return container;
	}
} 
