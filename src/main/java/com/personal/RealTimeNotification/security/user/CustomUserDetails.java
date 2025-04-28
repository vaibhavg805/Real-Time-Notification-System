package com.personal.RealTimeNotification.security.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.personal.RealTimeNotification.entity.Role;
import com.personal.RealTimeNotification.entity.User;

public class CustomUserDetails implements UserDetails,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	
	public CustomUserDetails(User user) {
		this.id=user.getId();
		this.name = user.getName();
		this.email=user.getEmail();
		this.password=user.getPassword();
		this.authorities = customGrantedAuthority(user.getRoles());
	}

	private Collection<? extends GrantedAuthority> customGrantedAuthority(Set<Role> roles2) {
		return roles2.stream().map(roles -> new SimpleGrantedAuthority(roles.getName()))
				.collect(Collectors.toList()); 
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Long getId() { 
		// I am doing this expose for SPEL
		return id;
	}

}
