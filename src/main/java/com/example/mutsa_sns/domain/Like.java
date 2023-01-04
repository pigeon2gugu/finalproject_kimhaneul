package com.example.mutsa_sns.domain;

import com.example.mutsa_sns.domain.dto.CommentDto;
import com.example.mutsa_sns.domain.dto.CommentModifyResponse;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "likes")
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
//soft delete
@SQLDelete(sql = "UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
//@Where(clause = "deleted_at is null")
public class Like extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void recoverLike(Like like) {
        this.deletedAt = null;
    }

    public static Like toEntity(User user, Post post) {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        return like;
    }

}
