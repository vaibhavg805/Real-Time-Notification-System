package com.personal.RealTimeNotification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPropertyConfig {
	
	private String secret;

}
