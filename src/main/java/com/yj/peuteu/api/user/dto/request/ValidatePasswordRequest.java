package com.yj.peuteu.api.user.dto.request;

import lombok.Data;

@Data
public class ValidatePasswordRequest {
    private String userId;
    private String password;
}