package com.hashedin.huspark.controller;

import com.hashedin.huspark.Mapper.UserRequest;
import com.hashedin.huspark.Mapper.UserResponse;
import com.hashedin.huspark.dto.AuthRequest;
import com.hashedin.huspark.dto.AuthResponse;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.service.AuthService;
import com.hashedin.huspark.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user) {
        ResponseEntity<UserResponse> createdUser = userService.createUser(user);
        return createdUser;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest request) throws Exception {
        return authService.authenticateUser(request);
    }
}
