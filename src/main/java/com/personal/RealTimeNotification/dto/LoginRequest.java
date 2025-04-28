package com.personal.RealTimeNotification.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

	private String name;
    private String email;
    private String password;  
    private Set<String> roles; 
}
