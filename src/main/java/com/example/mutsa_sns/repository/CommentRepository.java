package com.example.mutsa_sns.repository;

import com.example.mutsa_sns.domain.Comment;
import com.example.mutsa_sns.domain.Like;
import com.example.mutsa_sns.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findAllByPost(Post post, Pageable pageable);
    void deleteAllByPost(Post post);

    /*@Query("update Comment c set c.deletedAt = current_timestamp where c.post = :post")
    @Modifying(clearAutomatically = true)
    void deleteAllByPost(Post post);*/
}