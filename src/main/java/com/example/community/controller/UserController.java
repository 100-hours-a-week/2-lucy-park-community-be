package com.example.community.controller;

import com.example.community.dto.User.Request.UserSigninRequestDto;
import com.example.community.service.UserService;
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
}
