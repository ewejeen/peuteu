package com.yj.peuteu.common.login.service;

import com.yj.peuteu.common.jwt.domain.BlacklistedToken;
import com.yj.peuteu.common.jwt.service.BlacklistedTokenService;
import com.yj.peuteu.common.jwt.service.RefreshTokenService;
import com.yj.peuteu.common.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LogoutService {

    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;

    /**
     * 사용자 로그아웃 처리
     *
     * @param request
     */
    @Transactional
    public void logout(HttpServletRequest request) {
        // Access Token 블랙리스트 추가
        jwtUtil.extractAccessTokenFromHeader(request)
                .ifPresent(token ->
                        blacklistedTokenService.saveTokenToBlacklist(BlacklistedToken.builder()
                                .token(token)
                                .expiresAt(jwtUtil.getTokenExpiresAt(token))
                                .build())
                );

        // Refresh Token 삭제
        jwtUtil.extractRefreshTokenFromCookie(request)
                .ifPresent(refreshTokenService::deleteRefreshTokenByToken);
    }
}
