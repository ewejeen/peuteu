package com.yj.peuteu.common.jwt.service;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.jwt.domain.RefreshToken;
import com.yj.peuteu.common.jwt.dto.response.TokenResponse;
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
public class TokenRefreshService {
    private final JwtUtil jwtUtil;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Transactional
    public Optional<TokenResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = jwtUtil.extractRefreshToken(request);

        if (refreshTokenOpt.isEmpty() || !jwtUtil.isTokenValid(refreshTokenOpt.get())) {
            return Optional.empty();
        }

        String refreshToken = refreshTokenOpt.get();
        Optional<RefreshToken> tokenOpt = refreshTokenJpaRepository.findByToken(refreshToken);

        if(tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken token = tokenOpt.get();
        Optional<User> userOpt = userJpaRepository.findById(token.getUserId());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        String newAccessToken = jwtUtil.createAccessTokenById(user.getId());

        Date refreshExpiration = jwtUtil.getTokenExpiresAt(refreshToken);
        long daysRemaining = TimeUnit.MILLISECONDS.toDays(refreshExpiration.getTime() - new Date().getTime());

        if (daysRemaining <= jwtUtil.getMinRefreshAgeInDay()) {
            String newRefreshToken = jwtUtil.createRefreshToken();
            refreshTokenJpaRepository.deleteById(token.getId());
            refreshTokenJpaRepository.flush();
            refreshTokenJpaRepository.save(new RefreshToken(null, user.getId(), newRefreshToken, jwtUtil.getTokenExpiresAt(newRefreshToken)));

            jwtUtil.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
            return Optional.of(new TokenResponse(newAccessToken, newRefreshToken));
        }

        jwtUtil.sendAccessAndRefreshToken(response, newAccessToken, refreshToken);
        return Optional.of(new TokenResponse(newAccessToken, refreshToken));


       /* return refreshTokenJpaRepository.findByToken(refreshToken)
                .flatMap(token -> {
                    Optional<User> userOpt = userJpaRepository.findById(token.getUserId());
                    if (userOpt.isEmpty()) {
                        return Optional.empty();
                    }

                    User user = userOpt.get();
                    String newAccessToken = jwtUtil.createAccessTokenById(user.getId());

                    Date refreshExpiration = jwtUtil.getTokenExpiresAt(refreshToken);
                    long daysRemaining = TimeUnit.MILLISECONDS.toDays(refreshExpiration.getTime() - new Date().getTime());

                    if (daysRemaining <= jwtUtil.getMinRefreshAgeInDay()) {
                        String newRefreshToken = jwtUtil.createRefreshToken();
                        refreshTokenJpaRepository.deleteById(token.getId());
                        refreshTokenJpaRepository.flush();
                        refreshTokenJpaRepository.save(new RefreshToken(null, user.getId(), newRefreshToken, jwtUtil.getTokenExpiresAt(newRefreshToken)));

                        jwtUtil.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
                        return Optional.of(new TokenResponse(newAccessToken, newRefreshToken));
                    }

                    jwtUtil.sendAccessAndRefreshToken(response, newAccessToken, refreshToken);
                    return Optional.of(new TokenResponse(newAccessToken, refreshToken));
                });*/
    }
}
