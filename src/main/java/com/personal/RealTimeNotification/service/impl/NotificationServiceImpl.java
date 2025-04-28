package com.personal.RealTimeNotification.service.impl;


import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.personal.RealTimeNotification.constants.NotificationStatus;
import com.personal.RealTimeNotification.dto.NotificationDto;
import com.personal.RealTimeNotification.dto.ProcessingResponseDto;
import com.personal.RealTimeNotification.email.service.EmailService;
import com.personal.RealTimeNotification.entity.Notification;
import com.personal.RealTimeNotification.entity.User;
import com.personal.RealTimeNotification.exception.NotificationNotFoundException;
import com.personal.RealTimeNotification.exception.UserNotFoundException;
import com.personal.RealTimeNotification.mapper.NotificationMapper;
import com.personal.RealTimeNotification.redis.channel.RedisChannels;
import com.personal.RealTimeNotification.redis.event.EmailEvent;
import com.personal.RealTimeNotification.redis.event.EventPayload;
import com.personal.RealTimeNotification.redis.pub.interf.RedisPublisher;
import com.personal.RealTimeNotification.redis.service.RedisQueueService;
import com.personal.RealTimeNotification.repository.NotificationRepository;
import com.personal.RealTimeNotification.repository.UserRepository;
import com.personal.RealTimeNotification.service.NotificationService;
import com.personal.RealTimeNotification.undo.UndoService;

import jakarta.transaction.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
	private static final String USER_NOT_FOUND_TEMPLATE = " Not Exist in Records";
	private UserRepository userRepository;
	private NotificationMapper notificationMapper;
	private NotificationRepository notificationRepository;
	private RedisPublisher redisPublisher;
	//private final BlockingQueue<NotificationDto> queue;
	private final RedisQueueService redisQueueService;
    // private EmailService emailService;
	 private UndoService undoService;
	public NotificationServiceImpl(UserRepository userRepository,NotificationMapper notificationMapper,
									NotificationRepository notificationRepository,
									RedisPublisher redisPublisher,
									 BlockingQueue<NotificationDto> queue,
									 EmailService emailService,
									 RedisQueueService redisQueueService,
									 UndoService undoService) {
		this.userRepository=userRepository;
		this.notificationMapper=notificationMapper;
		this.notificationRepository=notificationRepository;
		this.redisPublisher=redisPublisher;
		//this.queue = queue;
	//	this.emailService=emailService;
		this.redisQueueService=redisQueueService;
		this.undoService=undoService;
	}

	@Override
	@Transactional
	@CacheEvict(value = "notification",key = "#userId")
	public ProcessingResponseDto<String> createNotification(Long userId, NotificationDto dto) {
		
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId+USER_NOT_FOUND_TEMPLATE));
		
		Notification notification = notificationMapper.convertDtoToNotificationEntity(dto);
		
		//setting user in notification entity
		notification.setUser(user);
		notification.setStatus(NotificationStatus.UNREAD);
		notification.setCreatedAt(Instant.now());
		
		//	notification = notificationRepository.save(notification);
		 // Instead of saving directly to DB
	   // queue.offer(dto); //  Push into BlockingQueue for background save
		
		dto.setId(null);
		dto.setUserId(userId); // yaha mene manually set kardi this is a fix 
		redisQueueService.pushInQueue(dto); // ham yaha  redis use karnenge 
		
	    LOGGER.info("Notification added to processing queue: {}", dto);
		//return notificationMapper.convertEntityToNotificationDto(notification);
	    
	    return new ProcessingResponseDto<>(true, "Notification request received and queued for processing");
	}

	@Override
	@Cacheable(value = "notification",key = "#userId")
	public List<NotificationDto> getAllNotifications(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId+USER_NOT_FOUND_TEMPLATE));
		
		List<Notification> notification = user.getNotifications();
		
		return notification.stream().map(notificationMapper::convertEntityToNotificationDto).collect(Collectors.toList());
		
	}

	@Override
	@Cacheable(value = "notification",key = "#userId")
	public List<NotificationDto> getUnreadNotifications(Long userId) {
		 userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId+USER_NOT_FOUND_TEMPLATE));
		
	List<Notification> notificationList = notificationRepository.findByUserIdAndStatus(userId, NotificationStatus.UNREAD);
	return notificationList.stream().map(notificationMapper::convertEntityToNotificationDto).collect(Collectors.toList());	
	}

	@Override
	@Transactional
	public void markAsRead(Long notificationId) {
	Notification notificationEntity = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationNotFoundException(notificationId+USER_NOT_FOUND_TEMPLATE));
	if (notificationEntity.getStatus() != NotificationStatus.READ) {
	    notificationEntity.setStatus(NotificationStatus.READ);
	    notificationEntity.setDeleted(false);
	    notificationRepository.save(notificationEntity);
	}
		Long userId = notificationEntity.getUser().getId();
		evictUserNotificationCache(userId);
	}

	@CacheEvict(value = "notification",key = "#userId")
	public void evictUserNotificationCache(Long userId) {
		LOGGER.info("Cache is Evicted for markAsread Method....");	
	}

	@Override
	@Transactional
	public String deleteNotification(Long notificationId) {
		Notification notificationEntity = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationNotFoundException(notificationId+USER_NOT_FOUND_TEMPLATE));
		//notificationRepository.delete(notificationEntity);	
		notificationEntity.setDeleted(true);
		notificationRepository.save(notificationEntity);
		Long userId = notificationEntity.getUser().getId();
		 undoService.markForDeletionWithDelay(userId);
		evictUserNotificationCache(userId);
		return "Notification marked for deletion. You have 1 minute to undo.";
	}

	
	/*
	 * @Override
	 * 
	 * @CacheEvict(value = "notification",key = "#userId") public NotificationDto
	 * publishNotification(Long userId, NotificationDto dto) { User user =
	 * userRepository.findById(userId).orElseThrow(() -> new
	 * UserNotFoundException(userId+USER_NOT_FOUND_TEMPLATE));
	 * 
	 * Notification notification =
	 * notificationMapper.convertDtoToNotificationEntity(dto);
	 * notification.setUser(user); notification.setCreatedAt(Instant.now());
	 * notification.setStatus(NotificationStatus.UNREAD); notification =
	 * notificationRepository.save(notification);
	 * 
	 * NotificationDto savedDto =
	 * notificationMapper.convertEntityToNotificationDto(notification);
	 * redisPublisher.publish(RedisChannels.NOTIFICATION_CHANNEL, savedDto);
	 * 
	 * //calling email if (user.getEmail() != null) { try {
	 * emailService.sendEmail(user.getEmail(),"Notification Saved, Welcome!"
	 * ,"Thanks"); } catch (Exception e) {
	 * 
	 * LOGGER.error("Failed to send email for notification to user: {}",
	 * user.getEmail(), e); } }
	 * 
	 * return savedDto; }
	 */
	 
	
	@Override
	@CacheEvict(value = "notification",key = "#userId")
	public NotificationDto publishNotification(Long userId, NotificationDto dto)  {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId+USER_NOT_FOUND_TEMPLATE));
		
		Notification notification = notificationMapper.convertDtoToNotificationEntity(dto);
		notification.setUser(user);
		notification.setCreatedAt(Instant.now());
		notification.setStatus(NotificationStatus.UNREAD);
	 //	notification =  notificationRepository.save(notification);
	 	
	 	NotificationDto  NotificationCreationRequestedEvent = notificationMapper.convertEntityToNotificationDto(notification);
	 	//Publishing notification:pubsub event
	 	redisPublisher.publish(RedisChannels.NOTIFICATION_CHANNEL, NotificationCreationRequestedEvent);
	 	
	 	// Publising Email Event
	 	if(user.getEmail() != null) {
	 		EventPayload<EmailEvent> emailPayload = new EventPayload<EmailEvent>("EMAIL_NOTIFICATION", new EmailEvent(user.getEmail(),"Notification Saved, Welcome!","Thanks for using me, I am best API in market"));
	 		redisPublisher.publish(RedisChannels.COMMON_EVENT_CHANNEL, emailPayload);
	 	}
	 	
	 	//Publishing Logging Event
	 	EventPayload<String> logPayload = new EventPayload<>("NOTIFICATION_LOG","Notification created for user: " + user.getId());
	 	    redisPublisher.publish(RedisChannels.COMMON_EVENT_CHANNEL, logPayload);
	
	 	return  NotificationCreationRequestedEvent;
	}
	
	

}
