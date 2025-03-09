package com.yj.peuteu.common.jwt.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTokenInfo {
    private String id;
    private String email;
    private String nickname;
}
