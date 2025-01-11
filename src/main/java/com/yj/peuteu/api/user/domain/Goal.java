package com.yj.peuteu.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Goal {
	HEALTH("건강 (체중 x 1g)"),
	MUSCLE("근성장 (체중 x 1.5~2g)"),
	ETC("수동 입력");

	private final String description;

	public static Goal ofValue(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return Arrays.stream(Goal.values())
			.filter(s -> s.name().equals(value))
			.findAny()
			.orElse(null);
	}
}
