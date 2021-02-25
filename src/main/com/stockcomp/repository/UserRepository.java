package com.stockcomp.repository;

import com.stockcomp.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
