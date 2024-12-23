package com.hashedin.huspark.service;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareConfiguration implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("OWNER").filter(s -> true);
    }


}
