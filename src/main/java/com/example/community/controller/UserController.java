package com.example.community.controller;

import com.example.community.dto.User.Request.*;
import com.example.community.dto.User.Response.UserLoginResponseDto;
import com.example.community.dto.User.Response.UserUpdateNicknameResponseDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.dto.Wrapper.WrapperResponse;
import com.example.community.dto.Wrapper.WrapperWithoutDataResponse;
import com.example.community.entity.User;
import com.example.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<WrapperWithoutDataResponse> registerUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
        userService.registerUser(requestDto);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("register_success");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/session")
    public ResponseEntity<WrapperResponse> loginUser(@Valid @RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto userLoginResponseDto = userService.loginUser(requestDto);
        WrapperResponse response = new WrapperResponse("login_success", userLoginResponseDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile/image")
    public ResponseEntity<WrapperResponse> updateProfileImage(@Valid @RequestBody UserUpdateProfileImageRequestDto requestDto) {
        UserUpdateProfileImageResponseDto userUpdateProfileImageResponseDto = userService.updateProfileImage(requestDto);
        WrapperResponse response = new WrapperResponse("update_profile_image_success", userUpdateProfileImageResponseDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile/nickname")
    public ResponseEntity<WrapperResponse> updateNickname(@Valid @RequestBody UserUpdateNicknameRequestDto requestDto) {
        UserUpdateNicknameResponseDto userUpdateNicknameResponseDto = userService.updateNickname(requestDto);
        WrapperResponse response = new WrapperResponse("update_nickname_success", userUpdateNicknameResponseDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile/password")
    public ResponseEntity<WrapperWithoutDataResponse> updatePassword(@Valid @RequestBody UserUpdatePasswordRequestDto requestDto) {
        userService.updatePassword(requestDto);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("update_password_success");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/session")
    public ResponseEntity<WrapperWithoutDataResponse> logoutUser() {
        User user = userService.logoutUser();
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("logout_success");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile/session")
    public ResponseEntity<WrapperWithoutDataResponse> unregisterUser() {
        User user = userService.unregisterUser();
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("회원 탈퇴 성공");
        return  ResponseEntity.ok(response);
    }
}
