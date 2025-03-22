package com.example.community.controller.UserControllerTest;

import com.example.community.controller.UserController;
import com.example.community.dto.User.Request.UserUpdatePasswordRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UpdatePasswordTest {
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
    @DisplayName("비밀번호 변경 성공 - 성공적으로 200 반환")
    void updatePassword_success() throws Exception {
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto("UpdatedPassword*");

        mockMvc.perform(patch("/users/profile/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updatePassword(any());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 잘못된 비밀번호 형식")
    void updatePassword_invalidPassword() throws Exception {
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto("invalid_password");

        mockMvc.perform(patch("/users/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updatePassword(any());
    }
}
