package com.personal.RealTimeNotification.redis.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailEvent {
    private String to;
    private String subject;
    private String body;

    public EmailEvent() {}

    public EmailEvent(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}

