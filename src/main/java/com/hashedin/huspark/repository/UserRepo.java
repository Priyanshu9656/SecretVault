package com.hashedin.huspark.repository;

import com.hashedin.huspark.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByname(String username);
    boolean existsByEmail(String email);

}
