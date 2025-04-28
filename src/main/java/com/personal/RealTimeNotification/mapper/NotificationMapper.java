package com.personal.RealTimeNotification.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.personal.RealTimeNotification.dto.NotificationDto;
import com.personal.RealTimeNotification.entity.Notification;
@Component
public class NotificationMapper {
	
	private ModelMapper modelMapper;
	public NotificationMapper(ModelMapper modelMapper) {
		this.modelMapper=modelMapper;
	}
	
	public NotificationDto convertEntityToNotificationDto(Notification notification) {
		return modelMapper.map(notification, NotificationDto.class);
	}
	
	public Notification convertDtoToNotificationEntity(NotificationDto notificationDto) {
		return modelMapper.map(notificationDto, Notification.class);
	}

}
