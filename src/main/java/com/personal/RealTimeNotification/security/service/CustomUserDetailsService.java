package com.personal.RealTimeNotification.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.RealTimeNotification.entity.User;
import com.personal.RealTimeNotification.repository.UserRepository;
import com.personal.RealTimeNotification.security.user.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		User user = userRepository.findByname(name).orElseThrow(() -> 
				new UsernameNotFoundException("User Not Found: " + name));
		
		return new CustomUserDetails(user);
		
	}

}
