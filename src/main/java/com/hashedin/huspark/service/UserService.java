package com.hashedin.huspark.service;


import com.hashedin.huspark.Mapper.UserRequest;
import com.hashedin.huspark.Mapper.UserResponse;
import com.hashedin.huspark.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<UserResponse> createUser(UserRequest user);
}
