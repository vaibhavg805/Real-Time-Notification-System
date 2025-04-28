package com.personal.RealTimeNotification.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.personal.RealTimeNotification.dto.ErrorResponse;
import com.personal.RealTimeNotification.dto.LoginRequest;
import com.personal.RealTimeNotification.dto.LoginResponse;
import com.personal.RealTimeNotification.dto.UserRequestDto;
import com.personal.RealTimeNotification.dto.UserResponseDto;
import com.personal.RealTimeNotification.exception.UserAlreadyExistException;
import com.personal.RealTimeNotification.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private AuthenticationService authenticationService;
	public AuthController(AuthenticationService authenticationService) {
		this.authenticationService=authenticationService;
	}
	
	@PostMapping("/register/user")
	public ResponseEntity<?> registerUserApi(@RequestBody UserRequestDto userDto) {
		 try {
		        UserResponseDto response = authenticationService.saveDataForUser(userDto);
		        return ResponseEntity.status(HttpStatus.CREATED).body(response);
		    } catch (UserAlreadyExistException e) {
		    	 ErrorResponse errorResponse = new ErrorResponse("User already exists", HttpStatus.BAD_REQUEST.value());
		         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		    } catch (Exception e) {
		    	  ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		    }
		
	}
	
	@PostMapping("/register/admin")
	public ResponseEntity<?> registerAdminApi(@RequestBody UserRequestDto userDto) {
		   try {
		        UserResponseDto response = authenticationService.saveDataForAdmin(userDto);
		        return ResponseEntity.status(HttpStatus.CREATED).body(response);
		    } catch (UserAlreadyExistException e) {
		    	 ErrorResponse errorResponse = new ErrorResponse("User already exists", HttpStatus.BAD_REQUEST.value());
		         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		    } catch (Exception e) {
		    	 ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		    }
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> loginAPi(@RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse =	authenticationService.login(loginRequest);
		 Map<String, String> response = new HashMap<String,String>();
		 
		 response.put("AccessToken", loginResponse.getAccessToken());
		 response.put("RefreshToken", loginResponse.getRefreshToken());
		 
		 return ResponseEntity.ok(response);
		 
	}

}
