package com.example.community.controller.UserControllerTest;

import com.example.community.controller.UserController;
import com.example.community.dto.User.Request.UserUpdateNicknameRequestDto;
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
public class UpdateNicknameTest {
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
    @DisplayName("닉네임 변경 성공 - 성공적으로 200 반환")
    void updateNickname_success() throws Exception {
        UserUpdateNicknameRequestDto requestDto = new UserUpdateNicknameRequestDto("nickname");

        mockMvc.perform(patch("/users/profile/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("update_nickname_success"));

        verify(userService, times(1)).updateNickname(any());
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 잘못된 닉네임 형식")
    void updateNickname_invalidNickname() throws Exception {
        UserUpdateNicknameRequestDto requestDto = new UserUpdateNicknameRequestDto("");

        mockMvc.perform(patch("/users/profile/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateNickname(any());
    }
}
