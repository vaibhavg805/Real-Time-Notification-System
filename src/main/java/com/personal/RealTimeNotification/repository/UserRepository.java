package com.personal.RealTimeNotification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.RealTimeNotification.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
		
	Optional<User> findByname(String name);
}
