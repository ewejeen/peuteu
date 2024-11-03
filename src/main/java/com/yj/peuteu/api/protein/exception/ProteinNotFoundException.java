package com.yj.peuteu.api.protein.exception;

import org.springframework.http.HttpStatus;

import com.yj.peuteu.common.exception.ApplicationException;

public class ProteinNotFoundException extends ApplicationException {
	protected ProteinNotFoundException(HttpStatus httpStatus, String message) {
		super(httpStatus, message);
	}

	public ProteinNotFoundException() {
		this(HttpStatus.NOT_FOUND, "해당 프로틴 정보가 없습니다.");
	}
}
