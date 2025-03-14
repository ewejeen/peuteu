package com.yj.peuteu.api.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ValidatePasswordRequest {
    private String userId;
    private String password;
}