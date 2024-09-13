package com.yj.peuteu.api.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    private String idx;
    private String email;
    private String password;
    private String nickname;

    //optional
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Double height;
    private Double weight;
    private String goal;
}
