package com.example.community.controller.UserControllerTest;

import com.example.community.controller.UserController;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
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
public class UpdateProfileImageTest {
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
    @DisplayName("프로필 이미지 변경 성공 - 성공적으로 200 반환")
    void updateProfileImageTest_success() throws Exception {
        UserUpdateProfileImageResponseDto responseDto = new UserUpdateProfileImageResponseDto("/uploads/thumbnail_another_image.jpg");

        mockMvc.perform(patch("/users/profile/image")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("update_profile_image_success"));

        verify(userService, times(1)).updateProfileImage(any());
    }

    @Test
    @DisplayName("프로필 이미지 변경 실패 - 잘못된 이미지 형식")
    void updateProfileImageTest_invalidImageUrl() throws Exception {
        UserUpdateProfileImageResponseDto responseDto = new UserUpdateProfileImageResponseDto("invalid_imageUrl");

        mockMvc.perform(patch("/users/profile/image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateProfileImage(any());
    }
}
