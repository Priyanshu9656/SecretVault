package com.hashedin.huspark.service;

import com.hashedin.huspark.Mapper.UserMapper;
import com.hashedin.huspark.Mapper.UserRequest;
import com.hashedin.huspark.Mapper.UserResponse;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.exception.BadRequestException;
import com.hashedin.huspark.exception.UserAlreadyExistsException;
import com.hashedin.huspark.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        if (Objects.isNull(userRequest)) {
            log.error("User Request is null");
            throw new BadRequestException("User Request is null");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + userRequest.getEmail());
        }

        User userToSave = userMapper.mapToUser(userRequest);
        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        User savedUser = userRepository.save(userToSave);

        UserResponse response = userMapper.mapToUserResponse(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
