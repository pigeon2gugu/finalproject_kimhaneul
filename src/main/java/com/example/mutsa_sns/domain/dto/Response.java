package com.example.mutsa_sns.domain.dto;

import com.example.mutsa_sns.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {

    private T result;
    private T errorResult;

    public static <T> Response<T> error(T errorResult) {
        return new Response("ERROR", errorResult);
    }

    public static <T> Response<T> success(T result) {
        return new Response("SUCCESS", result);
    }
}
