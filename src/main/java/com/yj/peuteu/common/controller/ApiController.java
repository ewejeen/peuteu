package com.yj.peuteu.common.controller;

import com.yj.peuteu.common.exception.BaseException;
import com.yj.peuteu.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ApiController {

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleBaseException(BaseException e) {
        return new ErrorResponse(e.isSuccess(), e.getDetailMessage());
    }
}
