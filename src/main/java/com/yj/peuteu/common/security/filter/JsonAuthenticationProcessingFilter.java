package com.yj.peuteu.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.peuteu.common.security.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/**
 * Spring Security의 JSON 요청 기반 로그인 처리 필터
 * - POST /api/login 주소로 로그인 요청을 받아 인증을 수행
 */
public class JsonAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher LOGIN_PATH_MATCHER = new AntPathRequestMatcher("/api/login", HttpMethod.POST.toString());
    private final ObjectMapper objectMapper;

    public JsonAuthenticationProcessingFilter(ObjectMapper objectMapper) {
        super(LOGIN_PATH_MATCHER); // AbstractAuthenticationProcessingFilter의 URL 매칭 설정 초기화
        this.objectMapper = objectMapper;
    }

    /**
     * JSON 형식의 로그인 요청 파라미터를 받아 인증을 수행
     * - 파라미터가 JSON이 아니면 Exception 발생
     * - 파라미터 값이 하나라도 비어 있다면 Exception 발생
     *
     * @param request  from which to extract parameters and perform the authentication
     * @param response the response, which may be needed if the implementation has to do a
     *                 redirect as part of a multi-stage authentication process (such as OIDC).
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!isJsonRequest(request.getContentType())) {
            throw new AuthenticationServiceException("Unsupported Content-Type: " + request.getContentType());
        }

        LoginRequest loginRequest = parseRequest(request);
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            throw new AuthenticationServiceException("Invalid login request");
        }

        return this
                .getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    /**
     * JSON 요청인지 확인
     *
     * @param requestContentType
     * @return
     */
    private boolean isJsonRequest(String requestContentType) {
        return requestContentType != null && requestContentType.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * LoginRequest로 파싱할 수 있는 JSON 요청인지 확인
     *
     * @param request
     * @return
     */
    private LoginRequest parseRequest(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse login request", e);
        }
    }
}