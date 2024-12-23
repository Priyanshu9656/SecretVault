package com.hashedin.huspark.Mapper;

import com.hashedin.huspark.dto.SecretRequest;
import com.hashedin.huspark.dto.SecretResponse;
import com.hashedin.huspark.entity.Secret;
import com.hashedin.huspark.repository.UserRepo;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecretMapper {

    @Autowired
    private UserRepo userRepository;

    public Secret mapToSecret(SecretRequest secretRequestDTO){
        if(Objects.isNull(secretRequestDTO)) {
            return new Secret();
        }

        return Secret.builder()
                .name(secretRequestDTO.getName())
                .description(secretRequestDTO.getDescription())
                .encryptedData(secretRequestDTO.getEncryptedData())
                .encryptionVersion(secretRequestDTO.getEncryptionVersion())
                .createdOn(secretRequestDTO.getCreatedOn())
                .lastModified(secretRequestDTO.getLastModified())
                .user(userRepository.findById(secretRequestDTO.getUser_id()).get())
                .build();
    }

    public SecretResponse mapToSecretResponseDto(Secret savedSecret) {
        if(Objects.isNull(savedSecret)) {
            return new SecretResponse();
        }

        return SecretResponse.builder()
                .name(savedSecret.getName())
                .description(savedSecret.getDescription())
                .encryptedData(savedSecret.getEncryptedData())
                .encryptionVersion(savedSecret.getEncryptionVersion())
                .createdOn(savedSecret.getCreatedOn())
                .lastModified(savedSecret.getLastModified())
                .build();
    }
}