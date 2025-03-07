package com.yj.peuteu.api.user.controller;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.application.LoginService;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.jwt.service.TokenRefreshService;
import com.yj.peuteu.common.jwt.util.JwtUtil;
import com.yj.peuteu.common.login.annotation.LoggedIn;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class LoginController {

    private final LoginService loginService;
    private final FindUserService findUserService;
    private final TokenRefreshService jwtService;

    /*@PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request, HttpServletResponse response) {
        System.out.println(request);

        loginService.login(request);
        String accessToken = jwtService.createAccessToken(request.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        findUserService.findUserEntityByEmail(request.getEmail())
                .updateRefreshToken(refreshToken);

        jwtService.setAccessTokenHeader(response, accessToken);

        return ApiResponse.ok();
    }*/

    @PostMapping("/refresh")
    public ResponseEntity refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.refreshAccessToken(request, response)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(400).build());
    }

    private void validateHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshTokenHeader = request.getHeader("Refresh-Token");
        if (Objects.isNull(authorizationHeader) || Objects.isNull(refreshTokenHeader)) {
            throw new JwtException("토큰이 없습니다.");
        }
    }
}

