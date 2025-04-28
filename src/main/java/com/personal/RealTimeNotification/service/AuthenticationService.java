package com.personal.RealTimeNotification.service;

import com.personal.RealTimeNotification.dto.LoginRequest;
import com.personal.RealTimeNotification.dto.LoginResponse;
import com.personal.RealTimeNotification.dto.UserRequestDto;
import com.personal.RealTimeNotification.dto.UserResponseDto;

public interface AuthenticationService {
	
	public UserResponseDto saveDataForUser(UserRequestDto userRequestDto);
	public UserResponseDto saveDataForAdmin(UserRequestDto userRequestDto);
	public LoginResponse login(LoginRequest loginRequest);

}
