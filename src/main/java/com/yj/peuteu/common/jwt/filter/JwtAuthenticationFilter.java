package com.yj.peuteu.common.jwt.filter;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.common.jwt.domain.TokenType;
import com.yj.peuteu.common.jwt.service.BlacklistedTokenService;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final FindUserService findUserService;
    private final BlacklistedTokenService blacklistedTokenService;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final List<String> WHITELIST = List.of("/api/login", "/api/join", "/api/logout", "/api/refresh");

    /**
     * 매 요청 시마다 로그인 여부를 확인<br>
     * * whitelist URL들은 로그인 여부 확인 제외
     * <p>
     * 1. Access Token이 유효한 경우: 인증 성공<br>
     * - 단, blacklist에 있는 토큰인 경우 인증 실패
     * 2. Access Token이 만료된 경우: 401 리턴 후 프론트에서 토큰 재발급 API 요청
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
        Optional<String> accessToken = jwtUtil.extractAccessTokenFromHeader(request);
        if (accessToken.isPresent()) {
            // blacklist(로그아웃된 토큰)인 경우 오류 발생
            if (blacklistedTokenService.existsByAccessToken(accessToken.get())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 그 외의 경우 유효한 토큰이면 인증 성공
            if (jwtUtil.isTokenValid(TokenType.ACCESS, accessToken.get())) {
                authenticateUser(accessToken.get());
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Access Token이 유효하지 않은 경우 401 반환 → 프론트에서 /api/refresh 호출
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // Access Token이 유효한 경우 유저 인증 처리
    private void authenticateUser(String accessToken) {
        jwtUtil.extractEmailFromAccessToken(accessToken)
                .ifPresent((email) -> {
                    UserDetailsImpl userDetails = new UserDetailsImpl(findUserService.findUserEntityByEmail(email));
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
    }
}