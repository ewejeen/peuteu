package com.yj.peuteu.api.user.controller;

import com.yj.peuteu.api.user.dto.request.LoginRequest;
import com.yj.peuteu.api.user.application.LoginService;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request) {
        System.out.println(request);
        loginService.login(request);
        return ApiResponse.ok();
    }
}

