package com.yj.peuteu.api.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String nickname;

    //optional
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Double height;
    private Double weight;
    @Enumerated(EnumType.STRING)
    private Goal goal;
}
