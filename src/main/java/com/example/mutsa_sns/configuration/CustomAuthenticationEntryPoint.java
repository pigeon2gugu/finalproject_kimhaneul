package com.example.mutsa_sns.configuration;

import com.example.mutsa_sns.domain.dto.ErrorResponse;
import com.example.mutsa_sns.domain.dto.Response;
import com.example.mutsa_sns.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        String exception = (String)request.getAttribute("exception");

        if(exception.equals(ErrorCode.INVALID_TOKEN.name())) {
            setResponse(response, ErrorCode.INVALID_TOKEN);
        }
        else if(exception.equals(ErrorCode.INVALID_PERMISSION.name())) {
            setResponse(response, ErrorCode.INVALID_PERMISSION);
        }

    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());

        response.getWriter().print(
                objectMapper.writeValueAsString(
                       Response.error(new ErrorResponse(errorCode.name(), errorCode.getMessage()))
                )
        );
    }
}
