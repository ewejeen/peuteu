package com.yj.peuteu.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Gender {
	MALE("남성"),
	FEMALE("여성"),
	X("미선택");

	private final String description;

	public static Gender ofValue(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return Arrays.stream(Gender.values())
			.filter(s -> s.name().equals(value))
			.findAny()
			.orElse(null);
	}
}
