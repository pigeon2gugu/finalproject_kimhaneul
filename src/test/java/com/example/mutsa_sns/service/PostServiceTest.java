package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.UserRole;
import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.repository.CommentRepository;
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
import static org.mockito.Mockito.*;

public class PostServiceTest {

    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    User user;
    Post post;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository, commentRepository);

        user = User.builder()
                .id(1)
                .userName("userName")
                .password("password")
                .role(UserRole.USER)
                .build();

        post = Post.builder()
                .id(1)
                .user(user)
                .title("title")
                .body("body")
                .build();



    }

    @Test
    @DisplayName("포스트 작성 성공")
    void post_success() {

        when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any()))
                .thenReturn(post);

        Assertions.assertDoesNotThrow(() -> postService.createPost(new PostCreateRequest(post.getTitle(), post.getBody()), user.getUserName()));

    }

    @Test
    @DisplayName("포스트 작성 실패 - 유저가 존재하지 않을 때")
    void post_fail() {

        when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.empty());

        when(postRepository.save(any()))
                .thenReturn(post);


        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.createPost(new PostCreateRequest(post.getTitle(), post.getBody()), user.getUserName());
                });

        Assertions.assertEquals("NOT_FOUNDED_USER_NAME", exception.getErrorCode().name());

    }

    @Test
    @DisplayName("조회 성공")
    void post_get_success() {

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostDto postDto = postService.detailPost(post.getId());

        //postDto userName == User userName
        assertEquals(user.getUserName(), postDto.getUserName());

    }

    @Test
    @DisplayName("수정 실패 - 포스트 존재하지 않음")
    void post_modify_fail1() {

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.modifyPost(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
                });

        Assertions.assertEquals("POST_NOT_FOUND", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("수정 실패 - 작성자!=유저")
    void post_modify_fail2() {

        //작성자
        User postCreator = User.builder()
                .id(2)
                .userName("userName2")
                .password("password2")
                .role(UserRole.USER)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(postCreator.getUserName())).thenReturn(Optional.of(postCreator));


        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.modifyPost(post.getId(), post.getTitle(), post.getBody(), postCreator.getUserName());
                });

        Assertions.assertEquals("INVALID_PERMISSION", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("수정 실패 - 유저 존재하지 않음")
    void post_modify_fail3() {

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.modifyPost(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
                });

        Assertions.assertEquals("NOT_FOUNDED_USER_NAME", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("삭제 실패 - 유저 존재하지 않음")
    void post_delete_fail1() {

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.deletePost(user.getUserName(), post.getId());
                });

        Assertions.assertEquals("NOT_FOUNDED_USER_NAME", exception.getErrorCode().name());

    }

    @Test
    @DisplayName("삭제 실패 - 포스트 존재하지 않음")
    void post_delete_fail2() {

        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.deletePost(user.getUserName(), post.getId());
                });

        Assertions.assertEquals("POST_NOT_FOUND", exception.getErrorCode().name());

    }

    @Test
    @DisplayName("삭제 실패 - 작성자!=유저")
    void post_delete_fail3() {

        //작성자
        User postCreator = User.builder()
                .id(2)
                .userName("userName2")
                .password("password2")
                .role(UserRole.USER)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUserName(postCreator.getUserName())).thenReturn(Optional.of(postCreator));

        AppException exception = assertThrows(AppException.class,
                ()-> {
                    postService.deletePost(postCreator.getUserName(), post.getId());
                });

        Assertions.assertEquals("INVALID_PERMISSION", exception.getErrorCode().name());

    }

}