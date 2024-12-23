package com.hashedin.huspark.service;

import com.hashedin.huspark.dto.AuthRequest;
import com.hashedin.huspark.dto.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    public ResponseEntity<AuthResponse> authenticateUser(AuthRequest authRequest) throws Exception;
}
