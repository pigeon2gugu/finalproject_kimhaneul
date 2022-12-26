package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    //포스트 작성 Test
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

    //포스트 상세 테스트
    @Test
    @DisplayName("1번글 조회 성공")
    @WithMockUser
    void post_detail_success() throws Exception {

        PostDto postDto = PostDto.builder()
                .id(1)
                .title("aaa")
                .body("bbb")
                .userName("ccc")
                .build();

        when(postService.detailPost(any())).thenReturn(postDto);

        mockMvc.perform(get("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(postDto.getId()))
                .andExpect(jsonPath("$.result.title").value(postDto.getTitle()))
                .andExpect(jsonPath("$.result.body").value(postDto.getBody()))
                .andExpect(jsonPath("$.result.userName").value(postDto.getUserName()))
                .andDo(print());

    }

    @Test
    @DisplayName("최신글 정렬 - pageable test")
    @WithMockUser
    void post_all_success() throws Exception {
        
        mockMvc.perform(get("/api/v1/posts")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andDo(print());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPostAll(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertEquals(Sort.by(DESC, "createdAt"), pageable.getSort());
        
    }


}