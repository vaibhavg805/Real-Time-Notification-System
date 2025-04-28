package com.personal.RealTimeNotification.redis.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

	 @Bean
	 RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String, Object> redis = new RedisTemplate<>();
		
		redis.setConnectionFactory(redisConnectionFactory);
		
		GenericJackson2JsonRedisSerializer jsonRedisSerializer = new 
												GenericJackson2JsonRedisSerializer(redisObjectMapper());
		
		redis.setKeySerializer(new StringRedisSerializer());
		redis.setValueSerializer(jsonRedisSerializer);
		redis.setHashKeySerializer(new StringRedisSerializer());
		redis.setHashValueSerializer(jsonRedisSerializer);
		
		redis.afterPropertiesSet();
		
		return redis;
		
	}
	
	
	@Bean
	ObjectMapper redisObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL,JsonAutoDetect.Visibility.ANY);
		mapper.activateDefaultTyping(
				LaissezFaireSubTypeValidator.instance,
				ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY
				);
		 mapper.registerModule(new JavaTimeModule());
	        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
	
	@Bean
    @Primary  
     ObjectMapper apiObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
	
	
	@Bean
	RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());
		
		RedisCacheConfiguration defaultCaching =	RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.ofMinutes(2))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
				.disableCachingNullValues();
		
		// Custom cache TTL for respective entity 		
		Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
		cacheConfigurationMap.put("notification", defaultCaching.entryTtl(Duration.ofMinutes(10)));		
		
		return RedisCacheManager.builder(redisConnectionFactory)
				.withInitialCacheConfigurations(cacheConfigurationMap)
				.cacheDefaults(defaultCaching)
				.build();
	}


}
