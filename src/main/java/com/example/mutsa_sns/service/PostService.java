package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.Comment;
import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.UserRole;
import com.example.mutsa_sns.domain.dto.*;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.CommentRepository;
import com.example.mutsa_sns.repository.PostRepository;
import com.example.mutsa_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

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

        post.modify(title, body);

        Post savedPost = postRepository.saveAndFlush(post);

        return savedPost.toResponse();

    }

    //댓글 기능
    public CommentDto createComment(Integer postId, String userName, String comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        Comment commentEntity = Comment.builder()
                .comment(comment)
                .user(user)
                .post(post)
                .build();

        commentRepository.save(commentEntity);

        return commentEntity.toResponse();

    }


    public Page<CommentDto> getComment(Integer postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));


        Page<Comment> commentPages = commentRepository.findAllByPost(post, pageable);

        return new PageImpl<>(commentPages.stream()
                .map(Comment::toResponse)
                .collect(Collectors.toList()));

    }

    public void deleteComment(String userName, Integer postId, Integer commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //userRole이 USER이고, 작성자와 삭제자 불일치시.
        //ADMIN은 모두 삭제 가능.
        if (user.getRole() == UserRole.USER && !Objects.equals(post.getUser().getUserName(), userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        //soft delete로 처리됨.
        commentRepository.delete(comment);

    }

    public CommentModifyResponse modifyComment(String userName, Integer postId, Integer commentId, String comment) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //userRole이 USER이고, 작성자와 삭제자 불일치시.
        //ADMIN은 모두 수정 가능.
        if (user.getRole() == UserRole.USER && !Objects.equals(post.getUser().getUserName(), userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        commentEntity.modify(comment);
        Comment savedComment = commentRepository.saveAndFlush(commentEntity);

        return savedComment.toModifiedResponse();
    }
}
