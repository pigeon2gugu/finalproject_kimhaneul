package com.example.mutsa_sns.domain.dto;

import com.example.mutsa_sns.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {

    private String resultCode;
    private T result;

    public static <T> Response<T> error(T result) {
        return new Response("ERROR", result);
    }

    public static <T> Response<T> success(T result) {
        return new Response("SUCCESS", result);
    }
}
