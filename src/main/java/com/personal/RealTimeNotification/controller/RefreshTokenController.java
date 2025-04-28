package com.personal.RealTimeNotification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.RealTimeNotification.dto.AccessToken;
import com.personal.RealTimeNotification.dto.LoginResponse;
import com.personal.RealTimeNotification.service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
public class RefreshTokenController {
	
	private RefreshTokenService refreshTokenService;
	
	 public RefreshTokenController(RefreshTokenService refreshTokenService) {
		this.refreshTokenService=refreshTokenService;
	}
	
	
	@PostMapping("/newToken")
	public ResponseEntity<LoginResponse> generateNewAccessToken(@RequestBody AccessToken newAccessToken) {
			LoginResponse loginResponse	= refreshTokenService.validateAndGenerateNewAccessToken(newAccessToken);
			return ResponseEntity.ok(loginResponse);
	}
	

}
