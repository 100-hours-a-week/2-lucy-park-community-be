package com.example.community.service;

import com.example.community.dto.User.Request.UserSigninRequestDto;
import com.example.community.entity.User;
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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(UserSigninRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if(requestDto.getImageUrl() == null) {
            throw new IllegalArgumentException("이미지를 먼저 업로드해주세요.");
        }

        // 비밀번호 암호화 구현 필요

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .nickname(requestDto.getNickname())
                .imageUrl(requestDto.getImageUrl())
                .build();

        return userRepository.save(user);
    }
}
