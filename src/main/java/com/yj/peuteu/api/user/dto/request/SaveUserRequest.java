package com.yj.peuteu.api.user.dto.request;

import com.yj.peuteu.api.user.domain.Gender;
import lombok.Data;

@Data
public class SaveUserRequest {
    private String name;
    private String email;
    private String password;
    private String nickname;
    private Gender gender;
    private Double height;
    private Double weight;
    private String goal;
}