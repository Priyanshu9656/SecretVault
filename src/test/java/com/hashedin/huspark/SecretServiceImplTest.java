package com.hashedin.huspark;

import com.hashedin.huspark.Mapper.SecretMapper;
import com.hashedin.huspark.constants.ApplicationConstants;
import com.hashedin.huspark.dto.SecretRequest;
import com.hashedin.huspark.dto.SecretResponse;
import com.hashedin.huspark.entity.Secret;
import com.hashedin.huspark.entity.SecretVersion;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.exception.SecretNotFoundException;
import com.hashedin.huspark.exception.UserNotFoundException;
import com.hashedin.huspark.repository.SecretRepo;
import com.hashedin.huspark.repository.SecretVersionRepo;
import com.hashedin.huspark.repository.UserRepo;
import com.hashedin.huspark.service.SecretServiceImpl;
import com.hashedin.huspark.utils.AESEncryptionDecryption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretServiceImplTest {

    @Mock
    private SecretRepo secretRepo;

    @Mock
    private SecretMapper secretMapper;

    @Mock
    private UserRepo userRepo;

    @Mock
    private SecretVersionRepo secretVersionRepo;

    @Mock
    private AESEncryptionDecryption aesEncryptionDecryption;

    @InjectMocks
    private SecretServiceImpl secretService;

    private SecretRequest secretRequest;
    private Secret secret;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(); // Initialize User as needed
        user.setId(1L);

        secretRequest = new SecretRequest();
        secretRequest.setUser_id(1L);
        secretRequest.setEncryptedData("plainData");

        secret = new Secret();
        secret.setId(1L);
        secret.setUser(user);
        secret.setEncryptedData("encryptedData");
    }

    @Test
    void createSecret_success() throws Exception {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(secretMapper.mapToSecret(any(SecretRequest.class))).thenReturn(secret);
        when(secretRepo.save(any(Secret.class))).thenReturn(secret);
        when(secretMapper.mapToSecretResponseDto(any(Secret.class))).thenReturn(new SecretResponse());

        SecretResponse responseDTO = secretService.createSecret(secretRequest);

        assertNotNull(responseDTO);
        verify(secretRepo, times(1)).save(any(Secret.class));
        verify(secretVersionRepo, times(1)).save(any(SecretVersion.class));
    }

    @Test
    void createSecret_UserNotFound() {
        when(userRepo.existsById(1L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            secretService.createSecret(secretRequest);
        });

        assertEquals("User Not Found with user_id: 1", exception.getMessage());
    }


    @Test
    void getSecretById_SecretNotFound() {
        when(secretRepo.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            secretService.getSecretById(1L);
        });

        assertEquals("No value present", exception.getMessage());
    }

    @Test
    void getSecretById_Success() throws Exception {
        when(secretRepo.findById(1L)).thenReturn(Optional.of(secret));
        when(aesEncryptionDecryption.decrypt("encryptedData", ApplicationConstants.SECRET_KEY)).thenReturn("plainData");
        when(secretMapper.mapToSecretResponseDto(secret)).thenReturn(new SecretResponse());

        SecretResponse response = secretService.getSecretById(1L);

        assertNotNull(response);
        verify(secretRepo).findById(1L);
    }

    @Test
    void updateSecret_SecretNotFound() {
        when(secretRepo.findById(1L)).thenReturn(Optional.empty());

        SecretNotFoundException exception = assertThrows(SecretNotFoundException.class, () -> {
            secretService.updateSecret(1L, secretRequest);
        });

        assertEquals("Secret not found with id: 1", exception.getMessage());
    }

    @Test
    void updateSecret_UserNotFound() {
        when(secretRepo.findById(1L)).thenReturn(Optional.of(secret));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            secretService.updateSecret(1L, secretRequest);
        });

        assertEquals("User not found with id: 1", exception.getMessage());
    }


    @Test
    void deleteSecret_SecretNotFound() {
        when(secretRepo.existsById(1L)).thenReturn(false);

        SecretNotFoundException exception = assertThrows(SecretNotFoundException.class, () -> {
            secretService.deleteSecret(1L);
        });

        assertEquals("Secret not found with id 1", exception.getMessage());
    }

    @Test
    void deleteSecret_Success() {
        when(secretRepo.existsById(1L)).thenReturn(true);

        boolean result = secretService.deleteSecret(1L);

        assertTrue(result);
        verify(secretRepo).deleteById(1L);
    }

}
