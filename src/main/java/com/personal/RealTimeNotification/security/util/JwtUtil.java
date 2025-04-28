package com.personal.RealTimeNotification.security.util;
import com.personal.RealTimeNotification.config.CustomPropertyConfig;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Component
public class JwtUtil {
	
	private String SECRET_KEY;
	private static final long JWT_EXPIRATION = 1000 * 60 * 15;
	private CustomPropertyConfig customPropertyConfig;
	
	public JwtUtil(CustomPropertyConfig customPropertyConfig) {
		this.SECRET_KEY = customPropertyConfig.getSecret();
	}
	
	private Key generateKey() {
		byte[] array = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(array);
	}
	
	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(Date.from(Instant.now()))
				.setExpiration(Date.from(Instant.now().plusSeconds(JWT_EXPIRATION/1000)))
				.signWith(generateKey())
				.compact();
	}
	
	public String extractUsername(String token) {
		return extractClaims(token,Claims::getSubject);
	}

	private <R> R extractClaims(String token, Function<Claims,R> claimsResolver) {
		final Claims getClaims = extractAllClaims(token);
		return claimsResolver.apply(getClaims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(generateKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public boolean validateToken(String username,String token) {
	return	(extractUsername(token).equals(username)) && !isValidate(token);
	}
	
	public boolean isValidate(String token) {
		Instant expirationTime = extractClaims(token, Claims::getExpiration).toInstant();
	    Instant currentTime = Instant.now();
	    
	    return expirationTime.isBefore(currentTime);
	}

}
