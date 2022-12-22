package com.example.mutsa_sns.service;


import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.dto.UserDto;
import com.example.mutsa_sns.domain.dto.UserJoinRequest;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.UserRepository;
import com.example.mutsa_sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}") // 환경변수내 정보 이용
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60; //만료시간 1h

    public UserDto join(UserJoinRequest req) {

        //중복 userName시 error
        userRepository.findByUserName(req.getUserName())
                .ifPresent(user -> {
                    //throw new AppException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName : %s는 이미 있습니다.", req.getUserName()));
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, "");
                });

        //UserRole index
        int index = 1;

        //db에 유저가 아무도 없을 때, 새로 생성되는 계정을 admin으로
        if (userRepository.count() == 0) {
            index = 0;
        }

        User savedUser = userRepository.save(req.toEntity(encoder.encode(req.getPassword()), index));

        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .role(savedUser.getRole())
                .build();
    }

    public String login(String userName, String password) {

        //userName이 존재하는가
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //password 일치하는가
        if(!encoder.matches(password, user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD ,"");
        }
        
        //위의 예외 발생하지 않으면 로그인 성공. token 발행

        return JwtTokenUtil.createToken(userName, secretKey, expireTimeMs);

    }

    public User tokenGetUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));
    }
}
