package com.personal.RealTimeNotification.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.personal.RealTimeNotification.security.service.AuthEntryPoint;
import com.personal.RealTimeNotification.security.service.CustomUserDetailsService;
import com.personal.RealTimeNotification.security.util.JwtFilter;


@Configuration
@Profile("prod")
public class ProdSecurityConfig {
    
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthEntryPoint authEntryPoint;
    private JwtFilter jwtFilter;
    public ProdSecurityConfig(CustomUserDetailsService customUserDetailsService,
                              AuthEntryPoint authEntryPoint,
                               JwtFilter jwtFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.jwtFilter=jwtFilter;
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(customUserDetailsService);
        return new ProviderManager(authProvider);
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(Customizer.withDefaults()) //  CSRF Enabled
          //  .cors(cors -> cors.configurationSource(corsConfigurationSource())) //  CORS Enabled
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register/user", "/auth/register/admin", "/auth/login").permitAll()
                .requestMatchers("/notification/**").authenticated()
                .requestMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
            	.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")) 
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) 
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)) 
                //  Enforce HTTPS
            )
            .build();
    }
    
    
	/*
	 * @Bean CorsConfigurationSource corsConfigurationSource() { CorsConfiguration
	 * configuration = new CorsConfiguration();
	 * configuration.setAllowedOrigins(List.of("https://dummy.com"));
	 * configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
	 * configuration.setAllowCredentials(true);
	 * configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control",
	 * "Content-Type"));
	 * 
	 * UrlBasedCorsConfigurationSource source = new
	 * UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",
	 * configuration); return source; }
	 */
}

