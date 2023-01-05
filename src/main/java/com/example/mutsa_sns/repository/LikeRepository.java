package com.example.mutsa_sns.repository;

import com.example.mutsa_sns.domain.Like;
import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Optional<Like> findByUserAndPost(User user, Post post);

    Optional<List<Like>> findByPost(Post post);

    /*@Query("update Like l set l.deletedAt = current_timestamp where l.deletedAt is null and l.post = :post")
    @Modifying(clearAutomatically = true)
    void deleteAllByPost(@Param("post") Post post);*/

    //Like 별칭을 like로 하면 error가 발생한다...
    @Query("SELECT COUNT(l) FROM Like l WHERE l.deletedAt is null and l.post = :post")
    Integer countByPost(@Param("post") Post post);
}