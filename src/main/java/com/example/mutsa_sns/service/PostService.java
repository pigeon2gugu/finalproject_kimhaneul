package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.dto.PostCreateRequest;
import com.example.mutsa_sns.domain.dto.PostDto;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.PostRepository;
import com.example.mutsa_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDto createPost(PostCreateRequest req, String userName) {

        log.info("userName:{}", userName);

        User user = userRepository.findByUserName(userName)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        log.info("user info:{}", user.getUserName());

        Post post = Post.builder()
                .user(user)
                .title(req.getTitle())
                .body(req.getBody())
                .build();

        postRepository.save(post);

        return PostDto.builder()
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .build();
    }
}