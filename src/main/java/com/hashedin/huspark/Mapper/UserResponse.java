package com.hashedin.huspark.Mapper;

import com.hashedin.huspark.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String username;
    private Set<Role> roles = new HashSet<>();
}
