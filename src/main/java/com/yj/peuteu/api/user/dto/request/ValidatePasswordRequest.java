package com.yj.peuteu.api.user.dto.request;

import lombok.Getter;

@Getter
public class ValidatePasswordRequest implements UserAssignRequest{
    private String userId;
    private String password;

    @Override
    public void assignUserId(String userId) {
        this.userId = userId;
    }
}