package com.yj.peuteu.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Goal {
    HEALTH("H", "건강 (체중 x 1g)"),
    MUSCLE("M", "근성장 (체중 x 1.5~2g)"),
    ETC("X", "수동 입력");

    private final String code;
    private final String description;

    public static Goal ofCode(String code) {
        if(!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(Goal.values())
                .filter(s -> s.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("존재하지 않는 값입니다. (%s)", code)));
    }
}
