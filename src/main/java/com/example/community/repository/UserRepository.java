package com.example.community.repository;

import com.example.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUsername(String username);
}
