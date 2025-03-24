package com.example.community.controller.CommentControllerTest;

import com.example.community.controller.CommentController;
import com.example.community.controller.PostController;
import com.example.community.service.CommentService;
import com.example.community.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UpdateCommentTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;
}
