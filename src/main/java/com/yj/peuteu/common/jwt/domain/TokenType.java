package com.yj.peuteu.common.jwt.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenType {
    ACCESS("액세스 토큰"),
    REFRESH("리프레시 토큰");

    private final String desc;
}
