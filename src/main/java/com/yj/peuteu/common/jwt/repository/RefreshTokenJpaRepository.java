package com.yj.peuteu.common.jwt.repository;

import com.yj.peuteu.common.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByToken(String token);

    Optional<RefreshToken> findByToken(String token);
}
