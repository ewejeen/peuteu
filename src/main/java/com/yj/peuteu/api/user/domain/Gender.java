package com.yj.peuteu.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("M", "남성"),
    FEMALE("F", "여성"),
    NOT_SELECTED("X", "미선택");

    private final String name;
    private final String description;
}
