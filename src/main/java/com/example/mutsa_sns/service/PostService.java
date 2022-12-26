package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.UserRole;
import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.PostRepository;
import com.example.mutsa_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDto createPost(PostCreateRequest req, String userName) {

        log.info("userName:{}", userName);

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        log.info("user info:{}", user.getUserName());

        Post post = Post.builder()
                .user(user)
                .title(req.getTitle())
                .body(req.getBody())
                .build();

        postRepository.save(post);

        return post.toResponse();
    }

    public PostDto detailPost(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        return post.toResponse();
    }

    public Page<PostDto> getPostAll(Pageable pageable) {
        Page<Post> postPages = postRepository.findAll(pageable);
        return new PageImpl<>(postPages.stream()
                .map(Post::toResponse)
                .collect(Collectors.toList()));

    }

    public void deletePost(String userName, Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));


        //userRole이 USER이고, 작성자와 삭제자 불일치시.
        //ADMIN은 모두 제거 가능
        if (user.getRole() == UserRole.USER && !Objects.equals(post.getUser().getUserName(), userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        postRepository.delete(post);

    }

    public PostDto modifyPost(Integer postId, String title, String body, String userName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));


        //userRole이 USER이고, 작성자와 삭제자 불일치시.
        //ADMIN은 모두 수정 가능.
        if (user.getRole() == UserRole.USER && !Objects.equals(post.getUser().getUserName(), userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        post.setTitle(title);
        post.setBody(body);

        Post savedPost = postRepository.saveAndFlush(post);

        return savedPost.toResponse();

    }

}
