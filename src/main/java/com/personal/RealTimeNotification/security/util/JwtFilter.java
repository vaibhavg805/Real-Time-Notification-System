package com.personal.RealTimeNotification.security.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.personal.RealTimeNotification.security.service.CustomUserDetailsService;
import com.personal.RealTimeNotification.security.user.CustomUserDetails;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	private JwtUtil jwtUtil;
	private CustomUserDetailsService customUserDetailsService;
	public JwtFilter(JwtUtil jwtUtil,
			CustomUserDetailsService customUserDetailsService) {
		this.jwtUtil=jwtUtil;
		this.customUserDetailsService=customUserDetailsService;
	}
	
	 private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader("Authorization");
		 
		 if(header != null && header.startsWith("Bearer ")) {
			 String token = header.substring(7);
			 
			 String username = jwtUtil.extractUsername(token);
			 
			 if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				 	CustomUserDetails customUserDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(username);
				 	
				 	if (jwtUtil.validateToken(customUserDetails.getUsername(), token)) {
						
				 		UsernamePasswordAuthenticationToken authenticationToken =
				 					new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());
				 		 authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				 			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				 	
				 	}
			 }
		 }
	
	 
	 filterChain.doFilter(request, response);
		
	}

}
