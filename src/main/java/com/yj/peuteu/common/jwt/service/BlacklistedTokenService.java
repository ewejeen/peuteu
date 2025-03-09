package com.yj.peuteu.common.jwt.service;

import com.yj.peuteu.common.jwt.repository.BlacklistedTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlacklistedTokenService {

    private final BlacklistedTokenJpaRepository blacklistedTokenJpaRepository;

    /**
     * 액세스 토큰이 블랙리스트에 등록되어 있는지 확인
     *
     * @param accessToken
     * @return
     */
    public boolean existsByAccessToken(String accessToken) {
        return blacklistedTokenJpaRepository.existsByToken(accessToken);
    }
}
