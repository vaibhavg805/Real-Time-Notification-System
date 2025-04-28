package com.personal.RealTimeNotification.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.personal.RealTimeNotification.security.service.AuthEntryPoint;
import com.personal.RealTimeNotification.security.service.CustomUserDetailsService;
import com.personal.RealTimeNotification.security.util.JwtFilter;


@Configuration
@Profile("dev")
@EnableWebSecurity
@EnableMethodSecurity
public class DevSecurityConfig {
	
	private CustomUserDetailsService customUserDetailsService;
	private AuthEntryPoint authEntryPoint;
	private JwtFilter jwtFilter;
	public DevSecurityConfig(CustomUserDetailsService customUserDetailsService,
								AuthEntryPoint authEntryPoint,
								JwtFilter jwtFilter) {
		this.customUserDetailsService=customUserDetailsService;
		this.authEntryPoint=authEntryPoint;
		this.jwtFilter=jwtFilter;
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// Authenticating User Credentials in Backend using AuthManager
	@Bean
	AuthenticationManager authenticationManager() {
		//System.out.println("Initializing AuthenticationManager...");
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setPasswordEncoder(passwordEncoder());
		//System.out.println("Password encoder set:"+passwordEncoder().getClass().getSimpleName()); 
		authProvider.setUserDetailsService(customUserDetailsService);
		//System.out.println("UserDetailsService set"+customUserDetailsService.getClass().getSimpleName());
		
		 ProviderManager providerManager = new ProviderManager(authProvider);
	      // System.out.println("AuthenticationManager initialized successfully.");

	        return providerManager;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
			return httpSecurity.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(auth -> auth
							.requestMatchers("/auth/register/user","/auth/register/admin","/auth/login").permitAll()
							.requestMatchers("/notifications/**").authenticated()
							.requestMatchers("/auth/newToken").permitAll()
							.requestMatchers("/user/**").hasAnyRole("ADMIN","USER")
							.requestMatchers("/admin/**").hasRole("ADMIN")
							.requestMatchers("/actuator/**").hasRole("ADMIN")
						
							.anyRequest().authenticated()
							)
					.exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
					
					.build();
		
		
	}

}
