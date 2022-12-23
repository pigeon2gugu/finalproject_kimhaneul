package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.domain.dto.UserDto;
import com.example.mutsa_sns.domain.dto.UserJoinRequest;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.service.PostService;
import com.example.mutsa_sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void post_success() throws Exception{
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.createPost(any(),any())).thenReturn(mock(PostDto.class));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postCreateRequest)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("포스트 작성 실패 - 로그인 하지 않은 상태(토큰 x)")
    @WithAnonymousUser
    void post_fail() throws Exception{

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("title")
                .body("body")
                .build();

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postCreateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

}