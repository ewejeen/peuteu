package com.yj.peuteu.common.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.peuteu.common.jwt.property.JwtProperties;
import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.jwt.domain.UserTokenInfo;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
//@Setter(value = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
@Service
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final FindUserService findUserService;
    private final ObjectMapper objectMapper;

    private String secret;
    private JwtProperties.JwtAccessProperties accessProperties;
    private JwtProperties.JwtRefreshProperties refreshProperties;

    @PostConstruct
    public void init() {
        secret = jwtProperties.getSecret();
        accessProperties = jwtProperties.getAccess();
        refreshProperties = jwtProperties.getRefresh();
    }

    /**
     * 액세스 토큰 생성 (이메일)
     *
     * @param userId
     * @return
     */
    public String createAccessTokenById(String userId) {
        User user = findUserService.findUserEntity(userId);
        UserTokenInfo userTokenInfo = UserTokenInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();

        return JWT.create()
                .withSubject(accessProperties.getSubject())
                .withIssuedAt(new Date())
                .withExpiresAt(createExpiresAt(accessProperties.getAge()))
                .withClaim(jwtProperties.getClaim(), objectMapper.convertValue(userTokenInfo, Map.class))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 액세스 토큰 생성 (UserTokenInfo)
     *
     * @param userTokenInfo
     * @return
     */
    public String createAccessToken(UserTokenInfo userTokenInfo) {
        return JWT.create()
                .withSubject(accessProperties.getSubject())
                .withIssuedAt(new Date())
                .withExpiresAt(createExpiresAt(accessProperties.getAge()))
                .withClaim(jwtProperties.getClaim(), objectMapper.convertValue(userTokenInfo, Map.class))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 리프레시 토큰 생성
     *
     * @return
     */
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(refreshProperties.getSubject())
                .withIssuedAt(new Date())
                .withExpiresAt(createExpiresAt(refreshProperties.getAge()))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * 액세스 토큰 + 리프레시 토큰 클라이언트에 전달
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenCookie(response, refreshToken);
    }

    /**
     * 액세스 토큰만 클라이언트에 전달
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
    }

    /**
     * 헤더에서 액세스 토큰 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessProperties.getHeader()))
                .filter(accessToken -> accessToken.startsWith(jwtProperties.getPrefix()))
                .map(accessToken -> accessToken.replace(jwtProperties.getPrefix(), ""));
    }

    /**
     * 쿠키에서 리프레시 토큰 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> refreshProperties.getSubject().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * 토큰에서 User email 추출
     *
     * @param accessToken
     * @return
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            Map<String, Object> userMap = JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(accessToken)
                    .getClaim(jwtProperties.getClaim())
                    .asMap();

            return Optional.ofNullable((String) userMap.get("email"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessProperties.getHeader(), jwtProperties.getPrefix() + accessToken);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        cookie.setPath("/"); // 모든 경로에서 쿠키 접근 가능
        cookie.setMaxAge(refreshProperties.getAge());
        response.addCookie(cookie);
    }

    /**
     * 토큰 유효 여부
     *
     * @param token
     * @return
     */
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("유효하지 않은 Token입니다. {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token 유효 여부 확인 오류가 발생했습니다. {}", e.getMessage());
            return false;
        }
    }

    public Date getTokenExpiresAt(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    public int getMinRefreshAgeInDay() {
        return refreshProperties.getMinAgeInDay();
    }


    private Date createExpiresAt(int ageInSeconds) {
        return Date.from(Instant.ofEpochMilli(new Date().getTime()).plus(Duration.ofSeconds(ageInSeconds)));
    }

}