package com.example.community.controller.UserControllerTest;

import com.example.community.controller.UserController;
import com.example.community.dto.User.Request.UserSigninRequestDto;
import com.example.community.security.JwtTokenProvider;
import com.example.community.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegisterUserTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private UserService userService;

    @SuppressWarnings("removal")
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 - 성공적으로 201 반환")
    void registerUser_success() throws Exception {
        UserSigninRequestDto requestDto = new UserSigninRequestDto("user@example.com", "Password123!", "닉네임", "/uploads/thumbnail_example.jpg");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("register_success"));

        verify(userService, times(1)).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 형식이 잘못됨")
    void registerUser_invalidEmail() throws Exception {
        UserSigninRequestDto requestDto = new UserSigninRequestDto("invalid-email", "password123!", "닉네임", "/uploads/thumbnail_example.jpg");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 형식이 잘못됨")
    void registerUser_invalidPassword() throws Exception {
        UserSigninRequestDto requestDto = new UserSigninRequestDto("user@example.com", "invalid-password", "닉네임", "/uploads/thumbnail_example.jpg");

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 형식이 잘못됨")
    void registerUser_invalidNickname() throws Exception {
        UserSigninRequestDto requestDto = new UserSigninRequestDto("user@example.com", "Password123!", "", "/uploads/thumbnail_example.jpg");

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미지 형식이 잘못됨")
    void registerUser_invalidImageUrl() throws Exception {
        UserSigninRequestDto requestDto = new UserSigninRequestDto("user@example.com", "Password123!", "닉네임", "/not_invalid_image.jpg");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - JSON 형식 아님")
    void registerUser_notJson() throws Exception {
        String invalidContent = "email=user@example.com";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(invalidContent))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 필드명")
    void registerUser_invalidFieldName() throws Exception {
        String requestWithWrongField = """
        {
            "emailAddress": "user@example.com",
            "password": "Password123!",
            "nickname": "닉네임",
            "thumbnailImage": "/uploads/thumbnail_example.jpg"
        }
        """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithWrongField))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 빈 JSON 객체")
    void registerUser_emptyJson() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }
}
