package com.yj.peuteu.common.jwt.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserTokenInfo {
    private String id;
    private String email;
    private String nickname;
}
