package com.personal.RealTimeNotification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.RealTimeNotification.entity.RefreshToken;
import com.personal.RealTimeNotification.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findBytoken(String token);
	void deleteByUser(User user);
}
