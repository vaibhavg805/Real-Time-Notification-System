package com.personal.RealTimeNotification.email.service;

public interface EmailService {
	 String sendEmail(String to, String subject, String body);
}
