package com.personal.RealTimeNotification.redis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.personal.RealTimeNotification.redis.ratelimiter.RateLimitInterceptor;

@Configuration
public class RateLimitConfig implements WebMvcConfigurer {
	
	private RateLimitInterceptor rateLimitInterceptor;
	
	public RateLimitConfig(RateLimitInterceptor rateLimitInterceptor) {
		this.rateLimitInterceptor=rateLimitInterceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(rateLimitInterceptor)
						.addPathPatterns("/notifications/**","/admin/**","/user/**")
						.excludePathPatterns("/auth/**");
	}
}
