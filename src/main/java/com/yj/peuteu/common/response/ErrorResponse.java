package com.yj.peuteu.common.response;


import lombok.Getter;

@Getter
public class ErrorResponse {
    private boolean isSuccess;
    private String message;

    public ErrorResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
