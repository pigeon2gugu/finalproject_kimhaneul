package com.example.mutsa_sns.exception;

import com.example.mutsa_sns.domain.dto.ErrorResponse;
import com.example.mutsa_sns.domain.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice //모든 컨트롤러의 예외를 처리
public class ExceptionManager {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> AppExceptionHandler(AppException e) {

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(new ErrorResponse(e.getErrorCode().name(), e.toString())));
    }

}
