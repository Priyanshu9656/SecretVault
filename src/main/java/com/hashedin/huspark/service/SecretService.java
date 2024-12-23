package com.hashedin.huspark.service;

import com.hashedin.huspark.dto.PageResponse;
import com.hashedin.huspark.dto.SecretRequest;
import com.hashedin.huspark.dto.SecretResponse;
import com.hashedin.huspark.entity.Secret;

import java.util.List;

public interface SecretService {

    SecretResponse createSecret(SecretRequest secretRequest);

    SecretResponse getSecretById(Long id);

    SecretResponse updateSecret(Long id, SecretRequest secretRequestDTO);

    boolean deleteSecret(Long id);

    List<SecretResponse> getAllSecret();

    PageResponse getAllSecretWithPagination(Integer pageNumber, Integer pageSize);

    PageResponse getAllSecretWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction);

}
