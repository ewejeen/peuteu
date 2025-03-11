package com.yj.peuteu.common.login.controller;

import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.login.annotation.NoAuthRequired;
import com.yj.peuteu.common.login.service.FindNoAuthRequiredUrlService;
import com.yj.peuteu.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@ApiController
public class AuthConfigController {
    private final FindNoAuthRequiredUrlService findNoAuthRequiredUrlService;

    @NoAuthRequired
    @GetMapping("/no-auth-required-urls")
    public ResponseEntity findNoAuthRequiredUrls() {
        return ApiResponse.data(findNoAuthRequiredUrlService.getNoAuthRequiredUrls());
    }
}