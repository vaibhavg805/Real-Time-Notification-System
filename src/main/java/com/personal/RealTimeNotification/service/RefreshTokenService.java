package com.personal.RealTimeNotification.service;

import com.personal.RealTimeNotification.dto.AccessToken;
import com.personal.RealTimeNotification.dto.LoginResponse;

public interface RefreshTokenService {

	public String generateRefreshToken(String username);
	public LoginResponse validateAndGenerateNewAccessToken(AccessToken accessToken);
}
