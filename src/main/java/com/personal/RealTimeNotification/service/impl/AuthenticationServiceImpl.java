package com.personal.RealTimeNotification.service.impl;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.RealTimeNotification.dto.LoginRequest;
import com.personal.RealTimeNotification.dto.LoginResponse;
import com.personal.RealTimeNotification.dto.UserRequestDto;
import com.personal.RealTimeNotification.dto.UserResponseDto;
import com.personal.RealTimeNotification.entity.Role;
import com.personal.RealTimeNotification.entity.User;
import com.personal.RealTimeNotification.exception.CustomAuthException;
import com.personal.RealTimeNotification.exception.RoleNotFoundException;
import com.personal.RealTimeNotification.exception.UserAlreadyExistException;
import com.personal.RealTimeNotification.mapper.UserMapper;
import com.personal.RealTimeNotification.repository.RoleRepository;
import com.personal.RealTimeNotification.repository.UserRepository;
import com.personal.RealTimeNotification.security.service.CustomUserDetailsService;
import com.personal.RealTimeNotification.security.user.CustomUserDetails;
import com.personal.RealTimeNotification.security.util.JwtUtil;
import com.personal.RealTimeNotification.service.AuthenticationService;
import com.personal.RealTimeNotification.service.RefreshTokenService;
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	
	private UserRepository userRepository;
	private UserMapper userMapper;
	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private AuthenticationManager authenticationManager;
	private JwtUtil jwtUtil;
	private RefreshTokenService refreshTokenService;
	private CustomUserDetailsService customUserDetailsService;
	
	
	public AuthenticationServiceImpl(UserRepository userRepository,
						UserMapper userMapper,
						PasswordEncoder passwordEncoder,
						RoleRepository roleRepository,
						AuthenticationManager authenticationManager,
						JwtUtil jwtUtil,
						RefreshTokenService refreshTokenService,
						CustomUserDetailsService customUserDetailsService) {
		this.userRepository=userRepository;
		this.userMapper=userMapper;
		this.passwordEncoder=passwordEncoder;
		this.roleRepository=roleRepository;
		this.jwtUtil=jwtUtil;
		this.refreshTokenService=refreshTokenService;
		this.customUserDetailsService=customUserDetailsService;
		this.authenticationManager=authenticationManager;
	}
	 private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Override
	public UserResponseDto saveDataForUser(UserRequestDto userRequestDto) {
		User user = userMapper.convertRequestDtoToEntity(userRequestDto);
		boolean alreadyExist = userRepository.findByname(user.getName()).isPresent();
		if(alreadyExist) {
			throw new UserAlreadyExistException("Username Already Exist");
		}
		if(user.getPassword() == null || user.getPassword().isEmpty()) {
			throw new IllegalArgumentException("Password Is Empty");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if(user.getRoles() == null) {
			user.setRoles(new HashSet<>());
		}
		Role userRole = roleRepository.findByname("ROLE_USER").orElseThrow(() ->new RoleNotFoundException("Role Not Found"));
		user.getRoles().add(userRole);
		User savedUser = userRepository.save(user);
		return userMapper.convertEntityToResponseDto(savedUser);
		
	}

	@Override
	public UserResponseDto saveDataForAdmin(UserRequestDto userRequestDto) {
		User user = userMapper.convertRequestDtoToEntity(userRequestDto);
		boolean alreadyExist = userRepository.findByname(user.getName()).isPresent();
		if(alreadyExist) {
			throw new UserAlreadyExistException("Username Already Exist");
		}
		if(user.getPassword() == null || user.getPassword().isEmpty()) {
			throw new IllegalArgumentException("Password Is Empty");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if(user.getRoles() == null) {
			user.setRoles(new HashSet<>());
		}
		Role userRole = roleRepository.findByname("ROLE_ADMIN").orElseThrow(() ->new RoleNotFoundException("Role Not Found"));
		user.getRoles().add(userRole);
		User savedUser = userRepository.save(user);
		return userMapper.convertEntityToResponseDto(savedUser);
	}

	
	  @Override 
	  public LoginResponse login(LoginRequest loginRequest) { 
	
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
					(loginRequest.getName(), loginRequest.getPassword()));
			
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getName());
			
			   LoginResponse loginResponse = new LoginResponse();
				String accesToken = jwtUtil.generateToken(userDetails.getUsername());
				loginResponse.setAccessToken(accesToken);
				
				String refreshToken = refreshTokenService.generateRefreshToken(userDetails.getUsername());
				loginResponse.setRefreshToken(refreshToken);
				
				return loginResponse;
	  
	 }
	 


	
}
