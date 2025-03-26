package com.example.community.controller.CommentControllerTest;

import com.example.community.controller.CommentController;
import com.example.community.controller.PostController;
import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.security.JwtTokenProvider;
import com.example.community.service.CommentService;
import com.example.community.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UpdateCommentTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private CommentService commentService;

    @SuppressWarnings("removal")
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 수정 성공 - 성공적으로 200 반환")
    void updateComment_success() throws Exception {
        CommentUpdateRequestDto requestDto = CommentUpdateRequestDto.builder()
                .content("this is updated comment.")
                .build();

        mockMvc.perform(patch("/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("edit_comment_success"));

        verify(commentService, times(1)).updateComment(eq(1L), eq(1L), refEq(requestDto));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 내용 형식 오류")
    void updateComment_InvalidContent() throws Exception {
        CommentUpdateRequestDto requestDto = CommentUpdateRequestDto.builder()
                .content("")
                .build();

        mockMvc.perform(patch("/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(eq(1L), eq(1L), refEq(requestDto));
    }
}
