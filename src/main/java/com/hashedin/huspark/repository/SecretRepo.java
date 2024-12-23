package com.hashedin.huspark.repository;

import com.hashedin.huspark.entity.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SecretRepo extends JpaRepository<Secret,Long> {
    List<Secret> findVersionsByUserId(Long id);

    List<Secret> findByUserId(Long id);
}
