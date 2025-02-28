package com.yj.peuteu.common.login.controller;

import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.login.service.LogoutService;
import com.yj.peuteu.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@ApiController
public class LogoutController {
    private final LogoutService logoutService;

    /**
     * 로그아웃
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        logoutService.logout(request);
        return ApiResponse.ok();
    }
}