package com.example.mutsa_sns.service;

import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.dto.UserDto;
import com.example.mutsa_sns.domain.dto.UserJoinRequest;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserDto join(UserJoinRequest req) {

        //중복 userName시 error
        userRepository.findByUserName(req.getUserName())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName : %s", req.getUserName()));
                });

        //UserRole index
        String role = "USER";

        //db에 유저가 아무도 없을 때, 새로 생성되는 계정을 admin으로
        if (userRepository.count() == 0) {
            role = "ADMIN";
        }

        Timestamp registeredAt = new Timestamp(System.currentTimeMillis());
        //한국시간 utc + 9h (timestamp + 32400000)
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        User savedUser = userRepository.save(req.toEntity(encoder.encode(req.getPassword()), role, registeredAt));

        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .role(savedUser.getRole())
                .build();
    }
}
