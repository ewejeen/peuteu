package com.yj.peuteu.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public ApplicationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApplicationException(String message) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
