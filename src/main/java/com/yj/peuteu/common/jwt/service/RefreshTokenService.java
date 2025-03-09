package com.yj.peuteu.common.jwt.service;

import com.yj.peuteu.common.jwt.domain.RefreshToken;
import com.yj.peuteu.common.jwt.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    /**
     * 토큰으로 검색
     *
     * @param token
     * @return
     */
    public RefreshToken findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token)
                .orElse(null);
    }

    /**
     * 리프레시 토큰 ID로 찾아 DB에서 삭제
     *
     * @param refreshTokenId
     */
    public void deleteRefreshTokenById(Long refreshTokenId) {
        refreshTokenJpaRepository.deleteById(refreshTokenId);
        refreshTokenJpaRepository.flush();
    }

    /**
     * 리프레시 토큰으로 찾아 DB에서 삭제
     *
     * @param refreshToken
     */
    public void deleteRefreshTokenByToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByToken(refreshToken);
    }

    /**
     * 리프레시 토큰 DB에 저장
     *
     * @param refreshToken
     */
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }
}
