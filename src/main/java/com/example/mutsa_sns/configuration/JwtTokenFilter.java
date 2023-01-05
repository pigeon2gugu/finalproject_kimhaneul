package com.example.mutsa_sns.configuration;

import com.example.mutsa_sns.domain.User;
import com.example.mutsa_sns.exception.AppException;
import com.example.mutsa_sns.exception.ErrorCode;
import com.example.mutsa_sns.service.UserService;
import com.example.mutsa_sns.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION); //헤더에 토큰을 넘긴 것을 가져옴.


        //토큰이 없는 경우
        if(authorizationHeader == null) {
            request.setAttribute("exception", ErrorCode.INVALID_PERMISSION.name());
            filterChain.doFilter(request, response);
            return;
        }

        //bearer로 시작하는 토큰이 아닌 경우
        if(!authorizationHeader.startsWith("Bearer ")) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
            filterChain.doFilter(request, response);
            return;
        }

        //bearer 이후 문자열 token 분리 성공 실패
        String token;

        try {
            token = authorizationHeader.split(" ")[1];

            //만료된 토큰일 경우
            if(JwtTokenUtil.isExpired(token, secretKey)) {
                request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
                filterChain.doFilter(request, response);
                return;
            };

            // Token에서 UserName꺼내기 (JwtTokenUtil에서 Claim에서 꺼냄)
            String userName = JwtTokenUtil.getUserName(token, secretKey);
            User user = userService.tokenGetUserByUserName(userName);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null
                    , List.of(new SimpleGrantedAuthority(user.getRole().name())));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //권한 부여
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);


        } catch (Exception e) {
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
            filterChain.doFilter(request, response);
        }




    }

}
