package com.personal.RealTimeNotification.email.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.personal.RealTimeNotification.email.service.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	private JavaMailSender mailSender;
	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender =mailSender;
	}
	@Override
	public String sendEmail(String to, String subject, String body) {
		
		try {
			MimeMessage  message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message,true);
			
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body,true);
			
			mailSender.send(message);
			
			return "Email Sent successfully to :" + to;
			
		}catch (Exception e) {
			LOGGER.error("Failed to send email to: {}", to, e);
		    return "Failed to send email to: " + to;
		}

	}

}
