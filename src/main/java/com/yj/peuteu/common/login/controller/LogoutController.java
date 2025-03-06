package com.yj.peuteu.common.login.controller;

import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.jwt.domain.BlacklistedToken;
import com.yj.peuteu.common.jwt.repository.BlacklistedTokenJpaRepository;
import com.yj.peuteu.common.jwt.repository.RefreshTokenJpaRepository;
import com.yj.peuteu.common.jwt.util.JwtUtil;
import com.yj.peuteu.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@ApiController
public class LogoutController {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final BlacklistedTokenJpaRepository blacklistedTokenJpaRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        // Access Token 블랙리스트 추가
        jwtUtil.extractAccessToken(request)
                .ifPresent(token -> blacklistedTokenJpaRepository.save(new BlacklistedToken(null, token, jwtUtil.getTokenExpiresAt(token))));

        // Refresh Token 삭제
        jwtUtil.extractRefreshToken(request)
                .ifPresent(token -> refreshTokenJpaRepository.deleteByToken(token));

        return ApiResponse.ok();
    }
}