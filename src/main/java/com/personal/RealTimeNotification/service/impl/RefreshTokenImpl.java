package com.personal.RealTimeNotification.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.RealTimeNotification.dto.AccessToken;
import com.personal.RealTimeNotification.dto.LoginResponse;
import com.personal.RealTimeNotification.entity.RefreshToken;
import com.personal.RealTimeNotification.entity.User;
import com.personal.RealTimeNotification.exception.RefreshTokenExpiredException;
import com.personal.RealTimeNotification.exception.RefreshTokenNotFoundException;
import com.personal.RealTimeNotification.exception.UserAlreadyExistException;
import com.personal.RealTimeNotification.repository.RefreshTokenRepository;
import com.personal.RealTimeNotification.repository.UserRepository;
import com.personal.RealTimeNotification.security.util.JwtUtil;
import com.personal.RealTimeNotification.service.RefreshTokenService;

@Service
public class RefreshTokenImpl implements RefreshTokenService{
	
	private RefreshTokenRepository refreshTokenRepository;
	private UserRepository userRepository;
	private JwtUtil jwtUtil;
	
	public RefreshTokenImpl(RefreshTokenRepository refreshTokenRepository,
			UserRepository userRepository,
			JwtUtil jwtUtil) {
		this.refreshTokenRepository=refreshTokenRepository;
		this.userRepository=userRepository;
		this.jwtUtil=jwtUtil;
	}

	@Override
	@Transactional
	public String generateRefreshToken(String username) {
		User customUser =  userRepository.findByname(username).orElseThrow(() -> new UserAlreadyExistException("User Not Found Exception:"));
		
		refreshTokenRepository.deleteByUser(customUser);
		RefreshToken refreshToken = new RefreshToken();
  		
  		refreshToken.setToken(UUID.randomUUID().toString());
  		refreshToken.setExpirationDate(Instant.now().plusSeconds(7*24*60*60));
  		refreshToken.setUser(customUser);
  		
  		refreshTokenRepository.save(refreshToken);
  		return refreshToken.getToken();
	}

	@Override
	@Transactional
	public LoginResponse validateAndGenerateNewAccessToken(AccessToken accessToken) {
		String logAccessToken = accessToken.getRefreshToken();
		
		 RefreshToken validateRefreshToken = refreshTokenRepository.findBytoken(logAccessToken).orElseThrow(() -> new RefreshTokenNotFoundException("Token Not Found"));
		 
		 if(validateRefreshToken.getExpirationDate().isBefore(Instant.now())) {
			 refreshTokenRepository.delete(validateRefreshToken);
			 throw new RefreshTokenExpiredException("Refresh Token Expired");
		 }
		 
		 refreshTokenRepository.delete(validateRefreshToken);
			String newRefreshToken = generateRefreshToken(validateRefreshToken.getUser().getName());
		 	String newJwtToken = jwtUtil.generateToken(validateRefreshToken.getUser().getName());
		 	
		 	LoginResponse loginResponse = new LoginResponse();
		 	loginResponse.setRefreshToken(newRefreshToken);
		 	loginResponse.setAccessToken(newJwtToken);
		 	
		 	return loginResponse;
		 
	}

}
