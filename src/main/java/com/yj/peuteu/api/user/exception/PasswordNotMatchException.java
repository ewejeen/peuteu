package com.yj.peuteu.api.user.exception;

import com.yj.peuteu.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends ApplicationException {
	protected PasswordNotMatchException(HttpStatus httpStatus, String message) {
		super(httpStatus, message);
	}

	public PasswordNotMatchException() {
		this(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
	}
}
