package com.yj.peuteu.api.user.dto.request;

import lombok.Data;

@Data
public class SaveUserRequest {
    private String email;
    private String password;
    private String nickname;
    private String gender;
    private Double height;
    private Double weight;
    private String goal;
}