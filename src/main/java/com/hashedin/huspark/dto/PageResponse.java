package com.hashedin.huspark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse {
    List<SecretResponse > secretResponseDTOList;
    Integer pageNumber;
    Integer pageSize;
    Long totalElements;
    int totalPages;
    boolean isLast;
}
