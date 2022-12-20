package com.example.mutsa_sns.domain.dto;


import com.example.mutsa_sns.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserJoinRequest {

    private String userName;
    private String password;

    public User toEntity(String password, String role, Timestamp time) {
        return User.builder()
                .userName(this.userName)
                .password(password)
                .role(role)
                .registeredAt(time)
                .build();
    }
}
