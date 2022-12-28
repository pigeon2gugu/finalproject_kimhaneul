package com.example.mutsa_sns.domain.dto;

import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserDto {

    private int id;
    private String userName;
    private String password;
    private UserRole role;

    public UserDto of(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }

}
