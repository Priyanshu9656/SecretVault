package com.hashedin.huspark;

import com.hashedin.huspark.Mapper.UserMapper;
import com.hashedin.huspark.Mapper.UserRequest;
import com.hashedin.huspark.Mapper.UserResponse;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.exception.BadRequestException;
import com.hashedin.huspark.exception.UserAlreadyExistsException;
import com.hashedin.huspark.repository.UserRepo;
import com.hashedin.huspark.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepo userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");


    }

    @Test
    void createUser_NullRequest() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(null);
        });

        assertEquals("User Request is null", exception.getMessage());
    }

    @Test
    void createUser_UserAlreadyExists() {
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(userRequest);
        });

        assertEquals("User already exists with email: test@example.com", exception.getMessage());
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userMapper.mapToUser(userRequest)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapToUserResponse(user)).thenReturn(userResponse);

        ResponseEntity response = userService.createUser(userRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository).save(user);
    }
}
