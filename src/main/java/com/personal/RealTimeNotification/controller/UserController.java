package com.personal.RealTimeNotification.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
		
	
	@GetMapping("/profile")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String getApi() {
		return "Hi!, Welcome to User Profile";
	}
	
}
