package com.example.community.controller;

import com.example.community.dto.User.Request.*;
import com.example.community.dto.User.Response.UserLoginResponseDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.entity.RefreshToken;
import com.example.community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserSigninRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok("회원가입에 성공하였습니다.");
    }

    @PostMapping("/session")
    public ResponseEntity<UserLoginResponseDto> loginUser(@Valid @RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto userLoginResponseDto = userService.loginUser(requestDto);
        return ResponseEntity.ok(userLoginResponseDto);
    }

    @PatchMapping("/profile/image")
    public ResponseEntity<UserUpdateProfileImageResponseDto> updateProfileImage(@Valid @RequestBody UserUpdateProfileImageRequestDto requestDto, HttpServletRequest request) {
        UserUpdateProfileImageResponseDto userUpdateProfileImageResponseDto = userService.updateProfileImage(requestDto, request);
        return ResponseEntity.ok(userUpdateProfileImageResponseDto);
    }

    @PatchMapping("/profile/nickname")
    public ResponseEntity<String> updateNickname(@Valid @RequestBody UserUpdateNicknameRequestDto requestDto, HttpServletRequest request) {
        userService.updateNickname(requestDto, request);
        return ResponseEntity.ok("닉네임 수정 성공하였습니다.");
    }

    @PatchMapping("/profile/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UserUpdatePasswordRequestDto requestDto, HttpServletRequest request) {
        userService.updatePassword(requestDto, request);
        return ResponseEntity.ok("비밀번호 수정 성공하였습니다.");
    }
}
