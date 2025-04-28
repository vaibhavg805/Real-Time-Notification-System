package com.personal.RealTimeNotification.redis.pub.interf;

public interface RedisPublisher {
	 void publish(String channel, Object message);
}
