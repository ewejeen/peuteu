package com.yj.peuteu.api.user.exception;

import com.yj.peuteu.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {
	protected UserNotFoundException(HttpStatus httpStatus, String message) {
		super(httpStatus, message);
	}

	public UserNotFoundException() {
		this(HttpStatus.NOT_FOUND, "해당 사용자 정보가 없습니다.");
	}
}
