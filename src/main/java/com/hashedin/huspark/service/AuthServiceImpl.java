package com.hashedin.huspark.service;

import com.hashedin.huspark.dto.AuthRequest;
import com.hashedin.huspark.dto.AuthResponse;
import com.hashedin.huspark.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserAuthDetailService userAuthDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    public ResponseEntity<AuthResponse> authenticateUser(AuthRequest authRequest) throws Exception {
        logger.info("Authenticating user: {}", authRequest.getUsername());

        authenticate(authRequest.getUsername(), authRequest.getPassword());

        final UserDetails userDetails = userAuthDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtUtils.generateJwtToken(userDetails);

        logger.info("User authenticated successfully: {}", authRequest.getUsername());
        return ResponseEntity.ok(AuthResponse.builder().token(token).tokenType("Bearer").build());
    }

    public void authenticate(String username, String password) throws Exception {
        logger.info("Authenticating credentials for user: {}", username);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            logger.info("Credentials authenticated successfully for user: {}", username);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", username, e);
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
