package com.example.mutsa_sns.controller;

import com.example.mutsa_sns.domain.UserRole;
import com.example.mutsa_sns.domain.dto.*;
import com.example.mutsa_sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

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

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest req) {
        String token = userService.login(req.getUserName(), req.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    //role 변경
    @GetMapping("/{userId}/role/change")
    public Response<UserRoleChangeResponse> roleChange(@PathVariable Integer userId, @ApiIgnore Authentication authentication) {
        String adminUserName = authentication.getName();
        UserDto userDto = userService.changeUserRole(userId, adminUserName);
        return Response.success(new UserRoleChangeResponse("ADMIN 부여 완료", userDto.getId()));
    }

}
