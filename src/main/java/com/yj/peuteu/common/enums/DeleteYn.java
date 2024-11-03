package com.yj.peuteu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeleteYn {
	Y("삭제"),
	N("미삭제");

	private final String description;
}
