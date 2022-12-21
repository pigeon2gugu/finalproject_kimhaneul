package com.example.mutsa_sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AppException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    @Override
    public String toString() {
        if(message.equals("")) {
            return errorCode.getMessage();
        }

        return String.format("%s, %s", errorCode.getMessage(), message);
    }


}
