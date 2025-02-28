package com.yj.peuteu.common.jwt.service;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.jwt.domain.RefreshToken;
import com.yj.peuteu.common.jwt.domain.TokenType;
import com.yj.peuteu.common.jwt.domain.UserTokenInfo;
import com.yj.peuteu.common.jwt.dto.response.TokenRefreshResponse;
import com.yj.peuteu.common.jwt.repository.RefreshTokenJpaRepository;
import com.yj.peuteu.common.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AccessTokenRefreshService {
    private final JwtUtil jwtUtil;
    private final FindUserService findUserService;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenService refreshTokenService;

    /**
     * 리프레시 토큰으로 액세스 토큰 재발급
     * - 쿠키에 리프레시 토큰이 없는 경우 > empty return
     * - 쿠키에 리프레시 토큰이 있는 경우
     * - DB에 리프레시 토큰 정보가 없는 경우 > empty return
     * - DB에도 정보가 있는 경우 재발급 진행!
     * <p>
     * - userId값으로 액세스 토큰 발급
     * - 리프레시 토큰의 잔여일이 minRefreshAgeInDay보다 작으면 리프레시 토큰도 재발급
     * - 재발급된 액세스 토큰과 리프레시 토큰을 헤더, 쿠키에 각각 세팅해 줌
     *
     * @param request
     * @param response
     * @return
     */
    @Transactional
    public Optional<TokenRefreshResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 리프레시 토큰 존재 여부 검사
        Optional<String> refreshTokenOpt = jwtUtil.extractRefreshTokenFromCookie(request);

        if (refreshTokenOpt.isEmpty() || !jwtUtil.isTokenValid(TokenType.REFRESH, refreshTokenOpt.get())) {
            return Optional.empty();
        }

        String refreshToken = refreshTokenOpt.get();
        Optional<RefreshToken> tokenOpt = refreshTokenJpaRepository.findByToken(refreshToken);

        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken token = tokenOpt.get();

        // 액세스 토큰 재발급
        User user = findUserService.findUserEntity(token.getUserId());
        UserTokenInfo userTokenInfo = UserTokenInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();

        String newAccessToken = jwtUtil.createAccessToken(userTokenInfo);
        jwtUtil.setAccessTokenHeader(response, newAccessToken);

        // 리프레시 토큰 잔여일이 얼마 남지 않은 경우 재발급
        long daysRemaining = TimeUnit.MILLISECONDS.toDays(jwtUtil.getTokenExpiresAt(refreshToken).getTime() - new Date().getTime());
        if (daysRemaining <= jwtUtil.getMinRefreshAgeInDay()) {
            refreshTokenService.deleteRefreshTokenById(token.getId());

            refreshToken = jwtUtil.createRefreshToken();
            refreshTokenService.saveRefreshToken(
                    RefreshToken.builder()
                            .userId(user.getId())
                            .token(refreshToken)
                            .expiresAt(jwtUtil.getTokenExpiresAt(refreshToken))
                            .build());
        }

        jwtUtil.setRefreshTokenCookie(response, refreshToken);
        return Optional.of(new TokenRefreshResponse(newAccessToken, refreshToken));
    }
}
