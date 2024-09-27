package com.yj.peuteu.api.user.controller;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.application.SaveUserService;
import com.yj.peuteu.api.user.dto.request.SaveUserRequest;
import com.yj.peuteu.common.controller.ApiController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class UserApiController {

    private final SaveUserService saveUserService;
    private final FindUserService findUserService;

    @PostMapping("/user")
    public ResponseEntity saveUser(@RequestBody SaveUserRequest request) {
        saveUserService.saveUser(request);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/user")
    public ResponseEntity findUser(String idx) {
        return ResponseEntity.ok(findUserService.findUser(idx));
    }
}

