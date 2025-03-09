package com.yj.peuteu.common.jwt.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class BlacklistedToken {
    @Column(name = "blacklisted_token_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String token;

    private Date expiresAt;
}
