package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.dto.Response;
import com.example.mutsa_sns.domain.dto.UserDto;
import com.example.mutsa_sns.domain.dto.UserJoinRequest;
import com.example.mutsa_sns.domain.dto.UserJoinResponse;
import com.example.mutsa_sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest req) {
        UserDto userDto = userService.join(req);
        return Response.success(new UserJoinResponse(userDto.getId(), userDto.getUserName()));
    }

}
