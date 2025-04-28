package com.personal.RealTimeNotification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.RealTimeNotification.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByname(String name);
}
