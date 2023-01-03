package com.example.mutsa_sns.domain;

import com.example.mutsa_sns.domain.dto.CommentDto;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "comment")
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
//soft delete
@SQLDelete(sql = "UPDATE comment SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is null")
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String comment;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public CommentDto toResponse() {
        return CommentDto.builder()
                .id(this.getId())
                .comment(this.getComment())
                .userName(this.getUser().getUserName())
                .postId(this.getPost().getId())
                .createdAt(this.getCreatedAt())
                .build();
    }

    public void modify(String comment) {
        this.comment = comment;
    }

}
