package com.example.mutsa_sns.service;


import com.example.mutsa_sns.domain.Post;
import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.domain.UserRole;
import com.example.mutsa_sns.domain.dto.UserDto;
import com.example.mutsa_sns.domain.dto.UserJoinRequest;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.repository.UserRepository;
import com.example.mutsa_sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
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

        return new UserDto().of(savedUser);
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

    @Transactional
    public UserDto changeUserRole(Integer userId, String adminUserName) {

        //admin userName이 존재하는가
        User adminUser = userRepository.findByUserName(adminUserName)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //adminUser의 유저 권한이 ADMIN이 아니면 exception
        if(adminUser.getRole() != UserRole.ADMIN) {
            throw new AppException(ErrorCode.INVALID_PERMISSION ,"");
        }

        //권한 바꿀 user의 userName이 존재하는가
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        //admin 권한 부여
        user.setRole(UserRole.ADMIN);

        User savedUser = userRepository.save(user);

        return new UserDto().of(savedUser);

    }
}
