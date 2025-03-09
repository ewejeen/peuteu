package com.yj.peuteu.common.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.peuteu.common.jwt.domain.TokenType;
import com.yj.peuteu.common.jwt.property.JwtProperties;
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

/**
 * JWT 토큰 관련 유틸
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class JwtUtil {

    private final ObjectMapper objectMapper;

    private final JwtProperties jwtProperties;
    private JwtProperties.JwtAccessProperties accessProperties;
    private JwtProperties.JwtRefreshProperties refreshProperties;
    private String secret;

    @PostConstruct
    public void init() {
        secret = jwtProperties.getSecret();
        accessProperties = jwtProperties.getAccess();
        refreshProperties = jwtProperties.getRefresh();
    }

    /**
     * 액세스 토큰 생성
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
     * 헤더에서 액세스 토큰 추출
     *
     * @param request
     * @return
     */
    public Optional<String> extractAccessTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessProperties.getHeader()))
                .filter(accessToken -> accessToken.startsWith(jwtProperties.getPrefix()))
                .map(accessToken -> accessToken.replace(jwtProperties.getPrefix(), ""));
    }

    /**
     * 쿠키에서 리프레시 토큰 추출
     *
     * @param request
     * @return
     */
    public Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> refreshProperties.getSubject().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * 토큰에서 사용자 email 추출
     *
     * @param accessToken
     * @return
     */
    public Optional<String> extractEmailFromAccessToken(String accessToken) {
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

    /**
     * 헤더에 액세스 토큰 세팅
     *
     * @param response
     * @param accessToken
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessProperties.getHeader(), jwtProperties.getPrefix() + accessToken);
    }

    /**
     * 쿠키에 리프레시 토큰 세팅
     *
     * @param response
     * @param refreshToken
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(refreshProperties.getSubject(), refreshToken);
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(refreshProperties.getAge());
        response.addCookie(cookie);
    }

    /**
     * 토큰 유효 여부 검사
     * @param tokenType
     * @param token
     * @return
     */
    public boolean isTokenValid(TokenType tokenType, String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("유효하지 않은 {}입니다. {}", tokenType.getDesc(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token 유효 여부 확인 오류가 발생했습니다. {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 만료일 검사
     *
     * @param token
     * @return
     */
    public Date getTokenExpiresAt(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    /**
     * 토큰 재발급 기준 잔여일 조회
     *
     * @return
     */
    public int getMinRefreshAgeInDay() {
        return refreshProperties.getMinAgeInDay();
    }

    // 토큰 만료일 생성
    private Date createExpiresAt(int ageInSeconds) {
        return Date.from(Instant.ofEpochMilli(new Date().getTime()).plus(Duration.ofSeconds(ageInSeconds)));
    }
}