package com.hashedin.huspark.repository;

import com.hashedin.huspark.entity.SecretVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretVersionRepo extends JpaRepository<SecretVersion,Long> {

}
