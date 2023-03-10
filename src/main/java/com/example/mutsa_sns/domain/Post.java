package com.example.mutsa_sns.domain;

import com.example.mutsa_sns.domain.dto.PostDto;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "post")
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
//soft delete
@SQLDelete(sql = "UPDATE post SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at is null")
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String body;
    private String title;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PostDto toResponse() {
        return PostDto.builder()
                .id(this.getId())
                .userName(this.getUser().getUserName())
                .title(this.getTitle())
                .body(this.getBody())
                .createdAt(this.getCreatedAt())
                .lastModifiedAt(this.getLastModifiedAt())
                .build();
    }

    public void modify(String title, String body) {
        this.title = title;
        this.body = body;
    }

}
