package com.personal.RealTimeNotification.redis.ratelimiter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.personal.RealTimeNotification.security.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	 
	private static final String BUCKET_KEY = "token_bucket:";
	
	private JwtUtil jwtUtil;
	private RedisRateLimiter redisRateLimiter;
	 public RateLimitInterceptor(RedisRateLimiter redisRateLimiter,JwtUtil jwtUtil) {
		this.redisRateLimiter=redisRateLimiter;
		this.jwtUtil=jwtUtil;
	}
	 
	 @Value("${rate.limit.protected.apis}")
	 private String[] protectedApis;
	
	
	 @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		 
	        String ip = request.getRemoteAddr();
	        String uri = request.getRequestURI();

	        String globalKey = BUCKET_KEY + ip;
	        String apiKey = BUCKET_KEY + ip + ":" + uri;
	        String token = null;
	        String authorizationHeader = request.getHeader("Authorization");
	        
	        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            token = authorizationHeader.substring(7); 
	        }
	        
	        String userId = jwtUtil.extractUsername(token);
	        
	     // User-level key (JWT-based rate limit)
	        String userKey = userId != null ? BUCKET_KEY + "user:" + userId : null;

	        
	        boolean isProtectedApi = Arrays.stream(protectedApis)
	                                       .anyMatch(uri::startsWith);

	        if (userId != null) {
	            if (!redisRateLimiter.allowRequest(userKey, true)) {  
	                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
	                response.getWriter().write("User rate limit exceeded. Try again later.");
	                return false;
	            }
	        }else {
	        	 if (isProtectedApi) {
	 	            if (!redisRateLimiter.allowRequest(apiKey, true)) {  
	 	                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
	 	                response.getWriter().write("API rate limit exceeded. Try again later.");
	 	                return false;
	 	            }
	 	        } else {
	 	            if (!redisRateLimiter.allowRequest(globalKey, false)) {  
	 	                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
	 	                response.getWriter().write("Global rate limit exceeded. Try again later.");
	 	                return false;
	 	            }
	 	        }
	        }
	        	
	        return true;
	    }

	}
	

