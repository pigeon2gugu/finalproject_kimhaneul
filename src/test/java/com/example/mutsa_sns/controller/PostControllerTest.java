package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.*;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

        PostDto createdPost = PostDto.builder()
                .id(1)
                .title(postCreateRequest.getTitle())
                .body(postCreateRequest.getBody())
                .build();

        when(postService.createPost(any(), any())).thenReturn(createdPost);

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
                .andExpect(jsonPath("$.result.postId").value(createdPost.getId()))
                .andDo(print());

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

    //댓글 기능
    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void comment_create_success() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        CommentDto createdComment = CommentDto.builder()
                .id(1)
                .postId(1)
                .comment(commentRequest.getComment())
                .build();

        when(postService.createComment(any(), any(), any())).thenReturn(createdComment);

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(createdComment.getId()))
                .andExpect(jsonPath("$.result.comment").value(createdComment.getComment()))
                .andExpect(jsonPath("$.result.postId").value(createdComment.getPostId()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 작성 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void comment_create_fail1() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 작성 실패 - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void comment_create_fail2() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(postService.createComment(any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void comment_modify_success() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("modified comment")
                .build();

        CommentModifyResponse modifiedComment = CommentModifyResponse.builder()
                .id(1)
                .postId(1)
                .comment(commentRequest.getComment())
                .build();

        when(postService.modifyComment(any(), any(), any(), any()))
                .thenReturn(modifiedComment);

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(modifiedComment.getId()))
                .andExpect(jsonPath("$.result.comment").value(modifiedComment.getComment()))
                .andExpect(jsonPath("$.result.postId").value(modifiedComment.getPostId()))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 인증 실패")
    @WithAnonymousUser
    void comment_modify_fail1() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("modified comment")
                .build();

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());


    }

    @Test
    @DisplayName("댓글 수정 실패 - Post없는 경우")
    @WithMockUser
    void comment_modify_fail2() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("modified comment")
                .build();

        when(postService.modifyComment(any(), any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자 불일치")
    @WithMockUser
    void comment_modify_fail3() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("modified comment")
                .build();

        when(postService.modifyComment(any(), any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.INVALID_PERMISSION.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PERMISSION.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 수정 실패 - 데이터베이스 에러")
    @WithMockUser
    void comment_modify_fail4() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("modified comment")
                .build();

        when(postService.modifyComment(any(), any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.DATABASE_ERROR.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.DATABASE_ERROR.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 삭제 성공")
    @WithMockUser
    void comment_delete_success() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf()))
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("댓글 삭제 완료"))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(status().isOk())
                .andDo(print());


    }

    @Test
    @DisplayName("댓글 삭제 실패 - 인증 실패")
    @WithAnonymousUser
    void comment_delete_fail1() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 삭제 실패 - Post없는 경우")
    @WithMockUser
    void comment_delete_fail2() throws Exception {

        doThrow(new AppException(ErrorCode.POST_NOT_FOUND, "")).when(postService).deleteComment(any(),any(),any());

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andDo(print());


    }

    @Test
    @DisplayName("댓글 삭제 실패 - 작성자 불일치")
    @WithMockUser
    void comment_delete_fail3() throws Exception {

        doThrow(new AppException(ErrorCode.INVALID_PERMISSION, "")).when(postService).deleteComment(any(),any(),any());

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.INVALID_PERMISSION.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.INVALID_PERMISSION.name()))
                .andDo(print());


    }

    @Test
    @DisplayName("댓글 삭제 실패 -  데이터베이스 에러")
    @WithMockUser
    void comment_delete_fail4() throws Exception {

        doThrow(new AppException(ErrorCode.DATABASE_ERROR, "")).when(postService).deleteComment(any(),any(),any());

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.DATABASE_ERROR.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.DATABASE_ERROR.name()))
                .andDo(print());


    }

    @Test
    @DisplayName("좋아요 누르기 성공")
    @WithMockUser
    void like_do_success() throws Exception {

        when(postService.doLike(any(), any()))
                .thenReturn("좋아요를 눌렀습니다.");

        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("좋아요를 눌렀습니다."))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void like_do_fail_1() throws Exception {

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @DisplayName("좋아요 누르기 실패 - 해당 Post가 없는 경우")
    @WithMockUser
    void like_do_fail_2() throws Exception {

        when(postService.doLike(any(), any()))
                .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));


        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andDo(print());

    }

    @Test
    @DisplayName("마이피드 조회 성공")
    @WithMockUser
    void myFeed_get_success() throws Exception {

        when(postService.getMyFeed(any(), any()))
                .thenReturn(Page.empty());


        mockMvc.perform(get("/api/v1/posts/my")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andDo(print());
    }


    @Test
    @DisplayName("마이피드 조회 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void myFeed_get_fail1() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/my")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }


}