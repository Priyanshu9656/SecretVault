package com.hashedin.huspark.service;

import com.hashedin.huspark.Mapper.SecretMapper;
import com.hashedin.huspark.constants.ApplicationConstants;
import com.hashedin.huspark.dto.PageResponse;
import com.hashedin.huspark.dto.SecretRequest;
import com.hashedin.huspark.dto.SecretResponse;
import com.hashedin.huspark.entity.ActionType;
import com.hashedin.huspark.entity.Secret;
import com.hashedin.huspark.entity.SecretVersion;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.exception.EncryptionDecryptionException;
import com.hashedin.huspark.exception.SecretNotFoundException;
import com.hashedin.huspark.exception.UserNotFoundException;
import com.hashedin.huspark.repository.SecretRepo;
import com.hashedin.huspark.repository.SecretVersionRepo;
import com.hashedin.huspark.repository.UserRepo;
import com.hashedin.huspark.utils.AESEncryptionDecryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SecretServiceImpl implements SecretService {

    private static final Logger logger = LoggerFactory.getLogger(SecretServiceImpl.class);

    @Autowired
    private SecretVersionRepo secretVersionRepo;

    @Autowired
    private SecretRepo secretRepo;

    @Autowired
    private SecretMapper secretMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AESEncryptionDecryption aesEncryptionDecryption;

    @Override
    public SecretResponse createSecret(SecretRequest secretRequest) {
        logger.info("Creating secret for user_id: {}", secretRequest.getUser_id());
        Long userId = secretRequest.getUser_id();
        if (!userRepo.existsById(userId)) {
            logger.error("User Not Found with user_id: {}", userId);
            throw new UserNotFoundException("User Not Found with user_id: " + userId);
        }
        Secret secret = secretMapper.mapToSecret(secretRequest);

        try {
            String encryptedData = aesEncryptionDecryption.encrypt(secretRequest.getEncryptedData(), ApplicationConstants.SECRET_KEY);
            secret.setEncryptedData(encryptedData);

            SecretVersion initialVersion = createInitialVersion(secret, secret.getEncryptedData());
            Secret savedSecret = secretRepo.save(secret);
            secretVersionRepo.save(initialVersion);

            String decryptedData = aesEncryptionDecryption.decrypt(savedSecret.getEncryptedData(), ApplicationConstants.SECRET_KEY);
            savedSecret.setEncryptedData(decryptedData);

            logger.info("Secret created successfully for user_id: {}", userId);
            return secretMapper.mapToSecretResponseDto(savedSecret);
        } catch (Exception e) {
            logger.error("Error during encryption/decryption process", e);
            throw new EncryptionDecryptionException("Error during encryption/decryption process", e);
        }
    }

    @Override
    public SecretResponse getSecretById(Long id) {
        logger.info("Fetching secret with id: {}", id);
        Optional<Secret> secret = secretRepo.findById(id);
        if (secret.isPresent()) {
            secret.get().setEncryptedData(aesEncryptionDecryption.decrypt(secret.get().getEncryptedData(), ApplicationConstants.SECRET_KEY));
            logger.info("Secret fetched successfully with id: {}", id);
            return secretMapper.mapToSecretResponseDto(secret.get());
        } else {
            logger.error("Secret not found with id: {}", id);
            throw new SecretNotFoundException("Secret not found with id: " + id);
        }
    }

    @Override
    public SecretResponse updateSecret(Long id, SecretRequest secretRequestDTO) {
        logger.info("Updating secret with id: {}", id);
        Secret optionalSecret = secretRepo.findById(id).orElseThrow(() -> new SecretNotFoundException("Secret not found with id: " + id));

        archiveCurrentVersion(optionalSecret);

        Secret secret = optionalSecret;
        secret.setName(secretRequestDTO.getName());
        secret.setDescription(secretRequestDTO.getDescription());
        secret.setEncryptedData(aesEncryptionDecryption.encrypt(secretRequestDTO.getEncryptedData(), ApplicationConstants.SECRET_KEY));
        secret.setCreatedOn(secretRequestDTO.getCreatedOn());
        secret.setLastModified(secretRequestDTO.getLastModified());
        secret.setEncryptionVersion(secretRequestDTO.getEncryptionVersion());

        Long userId = secretRequestDTO.getUser_id();
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            logger.error("User not found with id: {}", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        secret.setUser(optionalUser.get());

        Secret updatedSecret = secretRepo.save(secret);
        logger.info("Secret updated successfully with id: {}", id);
        return secretMapper.mapToSecretResponseDto(updatedSecret);
    }

    private SecretVersion createInitialVersion(Secret secret, String encryptedData) {
        SecretVersion version = new SecretVersion();
        version.setSecret(secret);
        version.setEncryptedData(encryptedData);
        version.setLastModified(LocalDate.now());
        version.setEncryptionVersion("initial version");
        version.setActionType(ActionType.CREATED);
        return version;
    }

    private void archiveCurrentVersion(Secret secret) {
        if (secret.getVersions().size() >= 10) {
            SecretVersion oldestVersion = secret.getVersions().get(0);
            secretVersionRepo.delete(oldestVersion);
            secret.getVersions().remove(0);
        }

        SecretVersion newVersion = new SecretVersion();
        newVersion.setSecret(secret);
        newVersion.setEncryptedData(secret.getEncryptedData());
        newVersion.setLastModified(secret.getLastModified());
        newVersion.setEncryptionVersion(secret.getEncryptionVersion());
        newVersion.setActionType(ActionType.UPDATED);
        secret.getVersions().add(newVersion);
    }

    public boolean deleteSecret(Long id) {
        logger.info("Deleting secret with id: {}", id);
        if (!secretRepo.existsById(id)) {
            logger.error("Secret not found with id: {}", id);
            throw new SecretNotFoundException("Secret not found with id " + id);
        }
        secretRepo.deleteById(id);
        logger.info("Secret deleted successfully with id: {}", id);
        return true;
    }

    @Override
    public List<SecretResponse> getAllSecret() {
        logger.info("Fetching all secrets");
        List<Secret> allSecrets = secretRepo.findAll();
        List<SecretResponse> secretResponses = allSecrets.stream()
                .map(secret -> secretMapper.mapToSecretResponseDto(secret))
                .collect(Collectors.toList());
        logger.info("Fetched all secrets successfully");
        return secretResponses;
    }

    public PageResponse getAllSecretWithPagination(Integer pageNumber, Integer pageSize) {
        logger.info("Fetching all secrets with pagination - Page number: {}, Page size: {}", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Secret> secretPages = secretRepo.findAll(pageable);
        List<Secret> allSecrets = secretPages.getContent();
        List<SecretResponse> allSecretResponseDto = allSecrets.stream()
                .map(secret -> secretMapper.mapToSecretResponseDto(secret))
                .collect(Collectors.toList());

        logger.info("Fetched all secrets with pagination successfully - Page number: {}, Page size: {}", pageNumber, pageSize);
        return PageResponse.builder()
                .secretResponseDTOList(allSecretResponseDto)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(secretPages.getTotalElements())
                .totalPages(secretPages.getTotalPages())
                .isLast(secretPages.isLast())
                .build();
    }

    public PageResponse getAllSecretWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction) {
        logger.info("Fetching all secrets with pagination and sorting - Page number: {}, Page size: {}, Sort by: {}, Direction: {}", pageNumber, pageSize, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Secret> secretPages = secretRepo.findAll(pageable);
        List<Secret> allSecrets = secretPages.getContent();

        List<SecretResponse> allSecretResponseDto = allSecrets.stream()
                .map(secret -> secretMapper.mapToSecretResponseDto(secret))
                .collect(Collectors.toList());

        logger.info("Fetched all secrets with pagination and sorting successfully - Page number: {}, Page size: {}, Sort by: {}, Direction: {}", pageNumber, pageSize, sortBy, direction);
        return PageResponse.builder()
                .secretResponseDTOList(allSecretResponseDto)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(secretPages.getTotalElements())
                .totalPages(secretPages.getTotalPages())
                .isLast(secretPages.isLast())
                .build();
    }
}
