package com.personal.RealTimeNotification.redis.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventPayload<T> {
	
	private String eventType;
	private T payload;
	
	public EventPayload(String eventType, T payload) {
		super();
		this.eventType = eventType;
		this.payload = payload;
	}

	public EventPayload() {
		super();
	}
	
	

}
