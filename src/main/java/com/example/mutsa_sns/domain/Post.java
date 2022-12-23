package com.example.mutsa_sns.domain;

import com.example.mutsa_sns.domain.dto.PostDto;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "post")
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String body;
    private String title;

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
}
