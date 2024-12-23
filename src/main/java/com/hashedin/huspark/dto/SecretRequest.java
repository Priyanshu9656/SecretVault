package com.hashedin.huspark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@Data
@AllArgsConstructor
@NoArgsConstructor@Builder
public class SecretRequest{
    private Long id;

    private String name;
    private String description;
    private String encryptedData;
    private LocalDate createdOn;
    private LocalDate lastModified;
    private String encryptionVersion;
    private Long user_id;
}
