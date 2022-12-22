package com.example.mutsa_sns.domain.dto;

import com.example.mutsa_sns.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostCreateRequest {
    private String title;
    private String body;
}
