package com.yj.peuteu.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("M", "남성"),
    FEMALE("F", "여성"),
    NOT_SELECTED("X", "미선택");

    private final String code;
    private final String description;

    public static Gender ofCode(String code) {
        if(!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(Gender.values())
                .filter(s -> s.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("존재하지 않는 값입니다. (%s)", code)));
    }
}
