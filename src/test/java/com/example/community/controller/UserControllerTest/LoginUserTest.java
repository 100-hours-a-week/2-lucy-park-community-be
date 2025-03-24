package com.example.community.controller.UserControllerTest;

import com.example.community.controller.UserController;
import com.example.community.dto.User.Request.UserLoginRequestDto;
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
public class LoginUserTest {
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
    @DisplayName("로그인 성공 - 성공적으로 200 반환")
    void loginUser_success() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("email@exmaple.com", "Password*");

        mockMvc.perform(post("/users/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("login_success"));

        verify(userService, times(1)).loginUser(any());
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 형식이 잘못됨")
    void loginUser_inValidEmail() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("invalid_email", "Password*");

        mockMvc.perform(post("/users/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).loginUser(any());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 형식이 잘못됨")
    void loginUser_inValidPassword() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("email@exmaple.com", "invalid_password");

        mockMvc.perform(post("/users/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).loginUser(any());
    }
}
