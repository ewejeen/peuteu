package com.yj.peuteu.common.exception;

import com.yj.peuteu.common.response.ApiResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

/**
 * ApplicationException <br>
 * - 입력값이나 중복 등의 예외사항이 필요한경우 상속 <br>
 * <br>
 * 그외 Exception <br>
 * - 위의 Exception 으로 잡지 못한 예상하지 못한 Exception <br>
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity applicationException(ApplicationException e, HttpServletRequest request) {
        logError(e, request);
        return ApiResponse.error(e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logError(e, request);

        // API 요청이 아닌 경우
        if (!request.getRequestURI().contains("api")) {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
            response.sendRedirect("/error");
            return null;
        }

        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private void logError(Exception e, HttpServletRequest request) {

        String format = "\n"
                + "################################################################################################\n"
                + "* method        : {}\n"
                + "* requestURI    : {}\n"
                + "* queryString   : {}\n"
                + "* exception     : {}\n"
                + "* message       : {}\n"
                + "################################################################################################";
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        log.error(format, method, requestURI, queryString, e.getClass().getName(), e.getMessage());

        // TODO 운영시 삭제
        e.printStackTrace();
    }
}
