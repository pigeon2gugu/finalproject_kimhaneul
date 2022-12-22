package com.example.mutsa_sns.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostDto {

    private int id;
    private String userName;
    private String title;
    private String body;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime createdAt;

}
