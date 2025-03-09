package com.yj.peuteu.api.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveUserRequest implements UserAssignRequest {
    private String id;
    private String email;
    private String password;
    private String nickname;
    private String gender;
    private Double height;
    private Double weight;
    private String goal;

    @Override
    public void assignUserId(String userId) {
        this.id = userId;
    }
}