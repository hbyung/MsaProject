package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll(); //반복적인 데이터
    UserDto getUserDetailByEmail(String email);
}
