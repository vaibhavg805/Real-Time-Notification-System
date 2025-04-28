package com.personal.RealTimeNotification.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.personal.RealTimeNotification.dto.UserRequestDto;
import com.personal.RealTimeNotification.dto.UserResponseDto;
import com.personal.RealTimeNotification.entity.User;

@Component
public class UserMapper {
	
	private final ModelMapper modelMapper;
	public UserMapper(ModelMapper modelMapper) {
		this.modelMapper=modelMapper;
	}
	
	 public User convertRequestDtoToEntity(UserRequestDto userRequestDto) {
		 return  modelMapper.map(userRequestDto, User.class);
	 }
	 
	 public UserResponseDto convertEntityToResponseDto(User user) {
		 return modelMapper.map(user, UserResponseDto.class);
	 }

}
