package com.personal.RealTimeNotification.redis.sub;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.RealTimeNotification.email.service.EmailService;
import com.personal.RealTimeNotification.redis.event.EmailEvent;
import com.personal.RealTimeNotification.redis.event.EventPayload;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommonEventSubscriber implements MessageListener {

	private final ObjectMapper objectMapper;
    private final EmailService emailService;
    
    public CommonEventSubscriber(@Qualifier("redisObjectMapper") ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String json = new String(message.getBody());
			EventPayload<?> event =  objectMapper.readValue(json,EventPayload.class);
			
			 switch (event.getEventType()) {
             case "EMAIL_NOTIFICATION":
                 EmailEvent emailEvent = objectMapper.convertValue(event.getPayload(), EmailEvent.class);
                 emailService.sendEmail(emailEvent.getTo(), emailEvent.getSubject(), emailEvent.getBody());
                 break;

             case "NOTIFICATION_LOG":
                 String logMsg = objectMapper.convertValue(event.getPayload(), String.class);
                 log.info("LOG_EVENT: {}", logMsg);
                 break;

             default:
                 log.warn("Unknown event type received: {}", event.getEventType());
         }
			
		} catch (Exception e) {
			 log.error("Failed to handle generic event: {}", e.getMessage());
		}
		
	}
    
    
}
