package com.yj.peuteu.common.jwt.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.jwt.domain.RefreshToken;
import com.yj.peuteu.common.jwt.repository.BlacklistedTokenJpaRepository;
import com.yj.peuteu.common.jwt.repository.RefreshTokenJpaRepository;
import com.yj.peuteu.common.jwt.util.JwtUtil;
import com.yj.peuteu.common.security.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtService;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final BlacklistedTokenJpaRepository blacklistedTokenJpaRepository;
    private final List<String> WHITELIST = List.of("/api/login", "/api/join", "/api/logout", "/api/refresh");
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /**
     * 매 요청 시마다 로그인 여부를 확인<br>
     * * whitelist URL들은 로그인 여부 확인 제외
     * <p>
     * 1. Access Token이 유효한 경우: 인증 성공<br>
     * 2. Access Token이 만료된 경우<br>
     * 2-1. Refresh Token이 유효: Access Token 재발급 및 인증 성공<br>
     * * 이 때 Refresh Token의 만료 기한이 3일 이내로 남은 경우 Refresh Token도 재발급<br>
     * 2-2. Refresh Token이 만료: 인증 실패
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // whitelist에 있는 경우 로그인 불필요
        if (WHITELIST.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Access Token이 유효한 경우 유저 인증 성공
        Optional<String> accessToken = jwtService.extractAccessToken(request);
        if (accessToken.isPresent()) {
            String accessTokenValue = accessToken.get();

            // blacklist인 경우 오류 발생
            if (blacklistedTokenJpaRepository.existsByToken(accessTokenValue)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 유효한 경우 인증 성공
            if (jwtService.isTokenValid(accessToken.get())) {
                authenticateUser(accessToken.get());
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Access Token이 만료된 경우 401 반환 → 프론트에서 /api/refresh 호출
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Access Token 만료된 경우 쿠키의 Refresh Token 검증
//        jwtService.extractRefreshToken(request)
//                .ifPresent(token -> checkRefreshTokenAndReIssueAccessToken(response, token));
//
//        filterChain.doFilter(request, response);
    }

    // Access Token이 유효한 경우 유저 인증 처리
    private void authenticateUser(String accessToken) {
        jwtService.extractEmail(accessToken)
                .flatMap(userJpaRepository::findByEmail)
                .ifPresent(this::saveAuthentication);
    }

    private void saveAuthentication(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Refresh Token이 유효하면 새로운 Access Token을 발급하고, 그렇지 않으면 401 반환
     */
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        log.info("Refresh Token 만료: {}", jwtService.getTokenExpiresAt(refreshToken));

        if (!jwtService.isTokenValid(refreshToken)) {
            log.warn("유효하지 않은 Refresh Token입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        refreshTokenJpaRepository.findByToken(refreshToken)
                .ifPresentOrElse(token -> {
                    log.warn("Refresh Token 유효 == Access Token 재발급.");
                    String newAccessToken = jwtService.createAccessTokenById(token.getUserId());

                    // 리프레시 토큰 날짜 확인
                    Date refreshExpiration = jwtService.getTokenExpiresAt(refreshToken);
                    long duration = refreshExpiration.getTime() - new Date().getTime();
                    long seconds = TimeUnit.MILLISECONDS.toDays(duration);
                    log.info("리프레시 토큰 남은 시간: {}일", seconds);

                    if (seconds <= jwtService.getMinRefreshAgeInDay()) {
                        log.info("리프레시 재발급!");
                        refreshTokenJpaRepository.deleteById(token.getId()); // 기존 리프레시는 삭제
                        String newRefreshToken = jwtService.createRefreshToken();
                        refreshTokenJpaRepository.save(new RefreshToken(null, token.getUserId(), newRefreshToken, jwtService.getTokenExpiresAt(newRefreshToken)));

                        jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
                    } else {
                        jwtService.sendAccessAndRefreshToken(response, newAccessToken, refreshToken);
                    }

                    authenticateUser(newAccessToken);
                }, () -> {
                    log.warn("Refresh Token을 가진 사용자를 찾을 수 없습니다.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                });

        // 쿠키의 토큰이 유효하니 DB에도 토큰이 있는지 확인
        /*userJpaRepository.findByRefreshToken(refreshToken)
                .ifPresentOrElse(user -> {
                    log.warn("Refresh Token 유효 == Access Token 재발급.");
                    String newAccessToken = jwtService.createAccessTokenById(user.getEmail());

                    // 리프레시 토큰 날짜 확인
                    Date refreshExpiration = jwtService.getRefreshTokenExpiration(refreshToken);
                    System.out.println("refreshExpiration = " + refreshExpiration);
                    System.out.println("new Date() = " + new Date());

                    long duration = refreshExpiration.getTime() - new Date().getTime();
                    long seconds = TimeUnit.MILLISECONDS.toDays(duration);
                    log.info("리프레시 토큰 남은 시간: {}일", seconds);
                    if (seconds <= 5) {
                        log.info("리프레시 재발급!");
                    }

                    jwtService.sendAccessAndRefreshToken(response, newAccessToken, refreshToken);
                    authenticateUser(newAccessToken);
                }, () -> {
                    log.warn("Refresh Token을 가진 사용자를 찾을 수 없습니다.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                });*/

    }
}