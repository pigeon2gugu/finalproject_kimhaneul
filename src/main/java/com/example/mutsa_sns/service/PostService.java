package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.*;
import com.example.mutsa_sns.domain.dto.*;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
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

    @Transactional
    public void deletePost(String userName, Integer postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));


        //userRole??? USER??????, ???????????? ????????? ????????????.
        //ADMIN??? ?????? ?????? ??????
        if (user.getRole() == UserRole.USER && !post.getUser().getUserName().equals(userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        //????????? ?????? ???????????? ????????? ????????? ???????????? ????????? ?????? ?????? ?????? (deleteAllByPost ?????? x)
        Optional<List<Like>> likes = likeRepository.findByPost(post);

        for (int i = 0; i < likes.get().size(); i++) {
            if (likes.get().get(i).getDeletedAt() == null) {
                likeRepository.delete(likes.get().get(i));
            }
        }

        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);

    }

    @Transactional
    public PostDto modifyPost(Integer postId, String title, String body, String userName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));


        //userRole??? USER??????, ???????????? ????????? ????????????.
        //ADMIN??? ?????? ?????? ??????.
        if (user.getRole() == UserRole.USER && !post.getUser().getUserName().equals(userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        post.modify(title, body);

        Post savedPost = postRepository.save(post);

        return savedPost.toResponse();

    }

    //?????? ??????
    @Transactional
    public CommentDto createComment(Integer postId, String userName, String comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        Comment commentEntity = Comment.toEntity(comment, user, post);

        commentRepository.save(commentEntity);

        //????????? ?????? ?????? ????????? comment ?????? ??? alarm ?????? (?????? ?????? x)
        if (post.getUser().getId() != user.getId()) {
            Alarm alarm = Alarm.toEntity(post, user, AlarmType.NEW_COMMENT_ON_POST);
            alarmRepository.save(alarm);
        }

        return commentEntity.toResponse();

    }


    public Page<CommentDto> getComment(Integer postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));


        Page<Comment> commentPages = commentRepository.findAllByPost(post, pageable);

        return commentPages.map(Comment::toResponse);


    }

    @Transactional
    public void deleteComment(String userName, Integer postId, Integer commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //userRole??? USER??????, ???????????? ????????? ????????????.
        //ADMIN??? ?????? ?????? ??????.
        if (user.getRole() == UserRole.USER && !post.getUser().getUserName().equals(userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        //soft delete??? ?????????.
        commentRepository.delete(comment);

    }

    @Transactional
    public CommentModifyResponse modifyComment(String userName, Integer postId, Integer commentId, String comment) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //userRole??? USER??????, ???????????? ????????? ????????????.
        //ADMIN??? ?????? ?????? ??????.
        if (user.getRole() == UserRole.USER && !post.getUser().getUserName().equals(userName)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "");
        }

        commentEntity.modify(comment);
        Comment savedComment = commentRepository.save(commentEntity);

        return savedComment.toModifiedResponse();
    }

    @Transactional
    public String doLike(Integer postId, String userName) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        Optional<Like> optLike = likeRepository.findByUserAndPost(user, post);

        if(optLike.isPresent()) {
            Like like = optLike.get();
            //???????????? ?????? ????????????, deletedAt??? null?????? (???????????? ?????? ??????)
            if(like.getDeletedAt() == null) {
                likeRepository.delete(like);
                return "???????????? ??????????????????.";
            }
            //???????????? ?????? ????????????, deletedAt??? null?????? (???????????? ?????? ??????)
            else {
                like.recoverLike(like); //????????? ?????? method
                likeRepository.save(like);
                return "???????????? ???????????????.";
            }
        }

        //???????????? ?????? ?????? ??? ?????? ??????
        likeRepository.save(Like.toEntity(user, post));

        //??? ????????? ??????????????? alarm ?????? + ?????? ????????? ????????? ?????? ????????? ??? (?????? ?????? x)
        if (post.getUser().getId() != user.getId()) {
            Alarm alarm = Alarm.toEntity(post, user, AlarmType.NEW_LIKE_ON_POST);
            alarmRepository.save(alarm);
        }

        return "???????????? ???????????????.";

    }

    public Integer getLike(Integer postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, ""));

        Integer counts = likeRepository.countByPost(post);
        return counts;

    }

    public Page<PostDto> getMyFeed(Pageable pageable, String userName) {

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        Page<Post> postPages = postRepository.findAllByUser(user, pageable);

        return postPages.map(Post::toResponse);

    }

}
