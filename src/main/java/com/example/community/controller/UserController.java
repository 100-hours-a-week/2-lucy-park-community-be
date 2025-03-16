package com.example.community.controller;

import com.example.community.dto.User.Request.*;
import com.example.community.dto.User.Response.UserLoginResponseDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.dto.Wrapper.WrapperResponse;
import com.example.community.dto.Wrapper.WrapperWithoutDataResponse;
import com.example.community.entity.RefreshToken;
import com.example.community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserSigninRequestDto requestDto) {
        userService.registerUser(requestDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입에 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/session")
    public ResponseEntity<WrapperResponse> loginUser(@Valid @RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto userLoginResponseDto = userService.loginUser(requestDto);
        WrapperResponse response = new WrapperResponse("login_success", userLoginResponseDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile/image")
    public ResponseEntity<WrapperResponse> updateProfileImage(@Valid @RequestBody UserUpdateProfileImageRequestDto requestDto, HttpServletRequest request) {
        UserUpdateProfileImageResponseDto userUpdateProfileImageResponseDto = userService.updateProfileImage(requestDto, request);
        WrapperResponse response = new WrapperResponse("update_profile_image_success", userUpdateProfileImageResponseDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile/nickname")
    public ResponseEntity<WrapperWithoutDataResponse> updateNickname(@Valid @RequestBody UserUpdateNicknameRequestDto requestDto, HttpServletRequest request) {
        userService.updateNickname(requestDto, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("닉네임 수정 성공하였습니다.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile/password")
    public ResponseEntity<WrapperWithoutDataResponse> updatePassword(@Valid @RequestBody UserUpdatePasswordRequestDto requestDto, HttpServletRequest request) {
        userService.updatePassword(requestDto, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("비밀번호 수정 성공하였습니다.");
        return ResponseEntity.ok(response);
    }
}
