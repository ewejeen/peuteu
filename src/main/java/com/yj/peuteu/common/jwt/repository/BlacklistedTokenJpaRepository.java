package com.yj.peuteu.common.jwt.repository;

import com.yj.peuteu.common.jwt.domain.BlacklistedToken;
import com.yj.peuteu.common.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedTokenJpaRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByToken(String token);
}
