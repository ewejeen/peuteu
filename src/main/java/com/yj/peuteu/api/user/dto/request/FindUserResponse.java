package com.yj.peuteu.api.user.dto.request;

import com.yj.peuteu.api.user.domain.Gender;
import com.yj.peuteu.api.user.domain.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FindUserResponse {
    private String email;
    private String nickname;
    private Gender gender;
    private Double height;
    private Double weight;
    private String goal;

    public static FindUserResponse from(User user) {
        return FindUserResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .goal(user.getGoal())
                .build();
    }
}