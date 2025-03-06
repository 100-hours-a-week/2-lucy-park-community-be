package com.example.community.service;

import com.example.community.domain.User;
import com.example.community.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }*/
}
