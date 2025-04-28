package com.personal.RealTimeNotification.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> userNotFoundExceptionMethod(UserNotFoundException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<String> userAlreadyExist(UserAlreadyExistException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	@ExceptionHandler(RoleNotFoundException.class)
	public ResponseEntity<String> roleNotFound(RoleNotFoundException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	@ExceptionHandler(RefreshTokenNotFoundException.class)
	public ResponseEntity<String> refreshTokenNotExist(RefreshTokenNotFoundException ex){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
	
	@ExceptionHandler(RefreshTokenExpiredException.class)
	public ResponseEntity<String> refreshTokenExpired(RefreshTokenExpiredException ex){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
	
	  @ExceptionHandler(CustomAuthException.class)
	    public ResponseEntity<Map<String, String>> handleAuthException(CustomAuthException ex) {
	        Map<String, String> response = new HashMap<>();
	        response.put("error", ex.getMessage());
	        return new ResponseEntity<>(response, ex.getStatus());
	    }
	  @ExceptionHandler(NotificationNotFoundException.class)
		public ResponseEntity<String> notificationNotFound(NotificationNotFoundException ex){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} 
	  
	

}
