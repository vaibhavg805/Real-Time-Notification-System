package com.personal.RealTimeNotification.redis.ratelimiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisRateLimiter {
 
	private static final int REFILL_RATE = 1;
	 private static final int BUCKET_CAPACITY_API = 4;
	 private static final int BUCKET_CAPACITY_GLOBAL = 10;
	 private static final long BUCKET_CAPACITY_USER = 4;
	private static final String FIELD_REFILL = "last_refill";
	private static final String FIELD_TOKEN = "tokens";
	
	private RedisTemplate<String, Object> redisTemplate;
	public RedisRateLimiter(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate=redisTemplate;
	}
	
	public boolean allowRequest(String key,boolean isApiLevel) {
		
		long bucketCapacity;
		if (key.contains("user:")) {
            bucketCapacity = BUCKET_CAPACITY_USER;  
        }else {
        	 bucketCapacity = isApiLevel ? BUCKET_CAPACITY_API : BUCKET_CAPACITY_GLOBAL;
        }
		
		Object refillObj = redisTemplate.opsForHash().get(key, FIELD_REFILL);
		Object tokensObj = redisTemplate.opsForHash().get(key, FIELD_TOKEN);
		
		Long lastRefill = parseLongSafely(refillObj);
	    Long tokensLeft = parseLongSafely(tokensObj);
		
		Long currentTime = System.currentTimeMillis() / 1000;
		if(lastRefill == null || tokensLeft == null) {
			redisTemplate.opsForHash().put(key, FIELD_REFILL , currentTime);
            redisTemplate.opsForHash().put(key, FIELD_TOKEN , bucketCapacity - 1);
            return true;
		}
		
		Long timeElapsed = currentTime - lastRefill;
		Long fillToken = Math.min(bucketCapacity, tokensLeft+(timeElapsed*REFILL_RATE));
		
		if(fillToken > 0) {
			tokensLeft = fillToken-1;
			redisTemplate.opsForHash().put(key, FIELD_REFILL, currentTime);
			redisTemplate.opsForHash().put(key, FIELD_TOKEN , tokensLeft);
			return true;
		}else {
            // Deny request
            return false;
        }
	}
	
	private Long parseLongSafely(Object obj) {
	    if (obj == null) return null;
	    try {
	        if (obj instanceof Long) {
	            return (Long) obj;
	        } else if (obj instanceof Integer) {
	            return ((Integer) obj).longValue();
	        } else {
	            return Long.parseLong(obj.toString());
	        }
	    } catch (NumberFormatException e) {
	       
	        return null;
	    }
	}
	
	
	
}
