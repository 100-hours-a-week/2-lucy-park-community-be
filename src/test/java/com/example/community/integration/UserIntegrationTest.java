package com.example.community.integration;

import com.example.community.dto.User.Request.UserLoginRequestDto;
import com.example.community.dto.User.Request.UserRegisterRequestDto;
import com.example.community.dto.User.Request.UserUpdateNicknameRequestDto;
import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 통합 테스트
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 -> 로그인 -> 닉네임 변경 통합 테스트[성공]")
    void userIntegrationTest_register_login_updateNickname_success() throws Exception {

        // 회원가입
        UserRegisterRequestDto registerRequestDto = UserRegisterRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .nickname("example")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("register_success"));

        // 로그인
        UserLoginRequestDto loginRequestDto = UserLoginRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .build();

        String loginResponse = mockMvc.perform(post("/users/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("login_success"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper
                .readTree(loginResponse)
                .get("data")
                .get("accessToken")
                .asText();

        // 닉네임 변경
        UserUpdateNicknameRequestDto nicknameRequestDto = UserUpdateNicknameRequestDto.builder()
                .nickname("updated")
                .build();

        mockMvc.perform(patch("/users/profile/nickname")
                        .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(nicknameRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("update_nickname_success"));


        // 닉네임 변경 확인
        User updatedUser = userRepository.findByEmail("test@email.com").orElseThrow();
        assertEquals("updated", updatedUser.getNickname());
    }
}
