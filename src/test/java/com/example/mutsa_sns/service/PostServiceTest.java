package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.UserRole;
import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.repository.PostRepository;
import com.example.mutsa_sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);

    }

    @Test
    @DisplayName("포스트 작성 성공")
    void post_success() {

        Post mockPostEntity = mock(Post.class);
        User mockUserEntity = mock(User.class);

        when(userRepository.findByUserName(mockUserEntity.getUserName()))
                .thenReturn(Optional.of(mockUserEntity));

        when(postRepository.save(any()))
                .thenReturn(mockPostEntity);

        Assertions.assertDoesNotThrow(() -> postService.createPost(new PostCreateRequest(mockPostEntity.getTitle(), mockPostEntity.getBody()), mockUserEntity.getUserName()));

    }

    @Test
    @DisplayName("포스트 작성 실패 - 유저가 존재하지 않을 때")
    void post_fail() {

        Post mockPostEntity = mock(Post.class);
        User mockUserEntity = mock(User.class);

        when(userRepository.findByUserName(mockUserEntity.getUserName()))
                .thenReturn(Optional.empty());

        when(postRepository.save(any()))
                .thenReturn(mockPostEntity);


        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.createPost(new PostCreateRequest(mockPostEntity.getTitle(), mockPostEntity.getBody()), mockUserEntity.getUserName());
                });

        Assertions.assertEquals("NOT_FOUNDED_USER_NAME", exception.getErrorCode().name());

    }

    @Test
    @DisplayName("조회 성공")
    void success_post_get() {

        User user = User.builder()
                .id(1)
                .userName("userName")
                .password("password")
                .role(UserRole.USER)
                .build();

        Post post = Post.builder()
                .id(1)
                .user(user)
                .title("title")
                .body("body")
                .build();


        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        PostDto postDto = postService.detailPost(post.getId());

        //postDto userName == User userName
        assertEquals(user.getUserName(), postDto.getUserName());



    }

}