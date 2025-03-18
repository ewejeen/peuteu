package com.yj.peuteu.common.jwt.controller;

import com.yj.peuteu.common.jwt.service.AccessTokenRefreshService;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.login.annotation.NoAuthRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class TokenController {

    private final AccessTokenRefreshService jwtService;

    /**
     * 액세스 토큰 재발급
     *
     * @param request
     * @param response
     * @return
     */
    @NoAuthRequired
    @PostMapping("/refresh")
    public ResponseEntity refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.refreshAccessToken(request, response)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(400).build());
    }
}

