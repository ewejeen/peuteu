package com.yj.peuteu.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public abstract class ApiResponse {
    private String result;
    private int resultCode;
    private String message;

    protected ApiResponse(String result, int resultCode, String message) {
        this.result = result;
        this.resultCode = resultCode;
        this.message = message;
    }

    // ============== SUCCESS ==============

    /**
     * 200 성공
     * - Body 없음
     *
     * @return 200 OK
     */
    public static ResponseEntity ok() {
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponseBuilder<>());
    }

    /**
     * 201 성공
     * - Body 없음
     *
     * @return 201 Created
     */
    public static ResponseEntity created() {
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponseBuilder<>());
    }

    /**
     * 200 Body Data가 있는 성공
     * - 단건 / 페이징 없는 다건
     *
     * @param data
     * @param <T>
     * @return 200 OK, data
     */
    public static <T> ResponseEntity data(T data) {
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponseBuilder<>(data));
    }

    /**
     * 200 Paged Body Data가 있는 성공
     * - 페이징 처리
     *
     * @param data
     * @param total 데이터의 총 건수
     * @param <T>
     * @return 200 OK, paged data
     */
    public static <T> ResponseEntity page(T data, Long total) {
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponseBuilder<>(data, total));
    }


    // ============== FAIL ==============

    /**
     * Http 에러 코드 미지정 실패
     *
     * @param httpStatus
     * @param message
     * @return
     */
    public static ResponseEntity error(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(new ErrorResponseBuilder(message));
    }

    /**
     * Http 에러 코드 지정 실패
     *
     * @param httpStatus
     * @param errorCode
     * @param message
     * @return
     */
    public static ResponseEntity error(HttpStatus httpStatus, HttpStatus errorCode, String message) {
        return ResponseEntity.status(httpStatus).body(new ErrorResponseBuilder(errorCode.value(), message));
    }

    // ============== Response Builder ==============
    @Getter
    static class SuccessResponseBuilder<E> extends ApiResponse {
        private E data;
        private Long total;

        public SuccessResponseBuilder() {
            this(null, null);
        }

        public SuccessResponseBuilder(E data) {
            this(data, null);
        }

        public SuccessResponseBuilder(E data, Long total) {
            super(ApiResponseEnum.OK.getResult(), HttpStatus.OK.value(), ApiResponseEnum.OK.getMessage());
            this.data = data;
            this.total = total;
        }
    }

    @Getter
    static class ErrorResponseBuilder extends ApiResponse {

        public ErrorResponseBuilder(String message) {
            super(ApiResponseEnum.FAIL.getResult(), HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        }

        public ErrorResponseBuilder(int errorCode, String message) {
            super(ApiResponseEnum.FAIL.getResult(), errorCode, message);
        }
    }
}
