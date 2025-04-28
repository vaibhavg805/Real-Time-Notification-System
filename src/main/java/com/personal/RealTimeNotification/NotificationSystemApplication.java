package com.personal.RealTimeNotification;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.personal.RealTimeNotification.entity.Role;
import com.personal.RealTimeNotification.repository.RoleRepository;

@SpringBootApplication
@EnableMethodSecurity
@EnableAsync
@EnableCaching
public class NotificationSystemApplication implements CommandLineRunner {

	private RoleRepository roleRepository;
	public NotificationSystemApplication(RoleRepository repository) {
		this.roleRepository=repository;
	}
	public static void main(String[] args) {
		SpringApplication.run(NotificationSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(roleRepository.findByname("ROLE_ADMIN").isEmpty()) {
			Role role = new Role();
			role.setName("ROLE_ADMIN");
			roleRepository.save(role);
		}
		if(roleRepository.findByname("ROLE_USER").isEmpty()) {
			Role role = new Role();
			role.setName("ROLE_USER");
			roleRepository.save(role);
		}
	}
	

}
