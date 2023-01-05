package com.example.mutsa_sns.repository;

import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    //Page<Post> findAllByUser(User user, Pageable pageable);
}