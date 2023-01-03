package com.example.mutsa_sns.repository;

import com.example.mutsa_sns.domain.Comment;
import com.example.mutsa_sns.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findAllByPost(Post post, Pageable pageable);
}