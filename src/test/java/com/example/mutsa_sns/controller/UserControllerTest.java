package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.UserDto;
import com.example.mutsa_sns.domain.dto.UserJoinRequest;
import com.example.mutsa_sns.domain.dto.UserLoginRequest;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join_success() throws Exception{
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("haneul")
                .password("1234")
                .build();

        UserDto joinedUser = UserDto.builder()
                .id(1)
                .userName(userJoinRequest.getUserName())
                .build();

        when(userService.join(any())).thenReturn(joinedUser);

        mockMvc.perform(post("/api/v1/users/join")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userName").value(userJoinRequest.getUserName())) //입력한 userName과 출력되는 userName이 같은지
                .andDo(print());

    }

    @Test
    @DisplayName("회원가입 실패 - userName 중복")
    @WithMockUser
    void join_fail() throws Exception{

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("haneul")
                .password("1234")
                .build();

        when(userService.join(any()))
                .thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser

    void login_success() throws Exception{

        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("haneul")
                .password("1234")
                .build();

        when(userService.login(any(), any()))
                .thenReturn("token");

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 실패 - userName 없음")
    @WithMockUser

    void login_fail1() throws Exception {

        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("haneul")
                .password("1234")
                .build();

        when(userService.login(any(), any()))
                .thenThrow(new AppException(ErrorCode.NOT_FOUNDED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - password 틀림")
    @WithMockUser

    void login_fail2() throws Exception {

        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("haneul")
                .password("1234")
                .build();

        when(userService.login(any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, ""));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }





}