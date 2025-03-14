package com.yj.peuteu.api.user.controller;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.application.SaveUserService;
import com.yj.peuteu.api.user.dto.request.SaveUserRequest;
import com.yj.peuteu.api.user.dto.request.ValidatePasswordRequest;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.jwt.domain.UserTokenInfo;
import com.yj.peuteu.common.login.annotation.LoggedIn;
import com.yj.peuteu.common.login.annotation.NoAuthRequired;
import com.yj.peuteu.common.response.ApiResponse;
import com.yj.peuteu.common.user.UserAssignRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class UserApiController {

    private final SaveUserService saveUserService;
    private final FindUserService findUserService;
    private final UserAssignRequestService userAssignRequestService;

    @NoAuthRequired
    @PostMapping("/join")
    public ResponseEntity saveUser(@RequestBody SaveUserRequest request) {
        return ApiResponse.created(saveUserService.saveUser(request));
    }

    @GetMapping("/user/{idx}")
    public ResponseEntity findUserById(@PathVariable String idx) {
        return ApiResponse.data(findUserService.findUserById(idx));
    }

    @GetMapping("/user/me")
    public ResponseEntity findUserMyself(@LoggedIn UserTokenInfo userTokenInfo) {
        return ApiResponse.data(findUserService.findUserById(userTokenInfo.getId()));
    }

    @GetMapping("/user")
    public ResponseEntity findAllUsers() {
        return ApiResponse.data(findUserService.findAllUsers());
    }

    @PatchMapping("/user")
    public ResponseEntity updateUser(@RequestBody SaveUserRequest request, @LoggedIn UserTokenInfo userTokenInfo) {
        userAssignRequestService.assignCurrentUser(request, userTokenInfo);
        saveUserService.updateUser(request);
        return ApiResponse.ok();
    }

    @GetMapping("/user/password")
    public ResponseEntity validatePassword(String password, @LoggedIn UserTokenInfo userTokenInfo) {
        ValidatePasswordRequest request = ValidatePasswordRequest.builder()
                .userId(userTokenInfo.getId())
                .password(password)
                .build();
        return ApiResponse.data(findUserService.validatePassword(request));
    }

    @NoAuthRequired
    @GetMapping("/user/check-email")
    public ResponseEntity checkEmail(String email) {
        return ApiResponse.data(findUserService.checkDuplicatedEmail(email));
    }

    @NoAuthRequired
    @GetMapping("/user/check-nickname")
    public ResponseEntity checkNickname(String nickname) {
        return ApiResponse.data(findUserService.checkDuplicatedNickname(nickname));
    }
}

