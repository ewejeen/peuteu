package com.yj.peuteu.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiResponseEnum {
    OK(HttpStatus.OK, "success", "성공"),
    FAIL(HttpStatus.OK, "fail", "실패");

    private final HttpStatus status;
    private final String result;
    private final String message;

    ApiResponseEnum(HttpStatus status, String result, String message) {
        this.status = status;
        this.result = result;
        this.message = message;
    }
}
