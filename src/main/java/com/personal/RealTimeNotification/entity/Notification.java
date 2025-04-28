package com.personal.RealTimeNotification.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.personal.RealTimeNotification.constants.NotificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String message;
	
	@Column(name = "is_deleted")
	private boolean isDeleted = false;
	
	@Enumerated(EnumType.STRING)
	private NotificationStatus status;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Instant createdAt;
	
	@ManyToOne
	@JoinColumn(name = "user_id",nullable = false)
	@JsonBackReference
	private User user;

	public Notification() {
		super();
		this.isDeleted = false;
	}
	

}
