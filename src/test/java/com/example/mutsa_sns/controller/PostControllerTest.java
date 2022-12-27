package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.domain.dto.PostModifyRequest;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void post_success() throws Exception {
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.createPost(any(), any())).thenReturn(mock(PostDto.class));

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
    void post_fail() throws Exception {

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

    @Test
    @DisplayName("post 수정 성공")
    @WithMockUser
    void post_modify_success() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("title")
                .body("body")
                .build();

        PostDto modifiedPost = PostDto.builder()
                .id(1)
                .title(postModifyRequest.getTitle())
                .body(postModifyRequest.getBody())
                .build();

        when(postService.modifyPost(any(), any(), any(), any())).thenReturn(modifiedPost);

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 수정 완료"))
                .andExpect(jsonPath("$.result.postId").value(modifiedPost.getId()))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("post 수정 실패 - 인증 실패")
    @WithAnonymousUser
    void post_modify_fail1() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("title")
                .body("body")
                .build();

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("post 수정 실패 - 작성자 불일치")
    @WithMockUser
    void post_modify_fail2() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.modifyPost(any(), any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.INVALID_PERMISSION.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PERMISSION.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("post 수정 실패 - 데이터 베이스 에러")
    @WithMockUser
    void post_modify_fail3() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.modifyPost(any(), any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.DATABASE_ERROR.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.DATABASE_ERROR.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("post 삭제 성공")
    @WithMockUser
    void post_delete_success() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("포스트 삭제 실패 - 인증 실패")
    @WithAnonymousUser
    void post_delete_fail1() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("포스트 삭제 실패 - 작성자 불일치")
    @WithMockUser
    void post_delete_fail2() throws Exception {

        doThrow(new AppException(ErrorCode.INVALID_PERMISSION, "")).when(postService).deletePost(any(),any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.INVALID_PERMISSION.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PERMISSION.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("포스트 삭제 실패 - 데이터베이스 에러")
    @WithMockUser
    void post_delete_fail3() throws Exception {

        doThrow(new AppException(ErrorCode.DATABASE_ERROR, "")).when(postService).deletePost(any(),any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.DATABASE_ERROR.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.DATABASE_ERROR.name()))
                .andDo(print());
    }

}