package com.yj.peuteu.api.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
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

    @Column(length = 1000)
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    // 패스워드 암호화
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
}
