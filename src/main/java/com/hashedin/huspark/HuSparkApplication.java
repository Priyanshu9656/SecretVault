package com.hashedin.huspark;

import com.hashedin.huspark.service.AuditorAwareConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing(auditorAwareRef="auditorAware")
public class HuSparkApplication {

	private static final Logger logger= LoggerFactory.getLogger(HuSparkApplication.class);

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareConfiguration();
	}

	public static void main(String[] args) {
		SpringApplication.run(HuSparkApplication.class, args);
	}

}
