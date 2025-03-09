package com.yj.peuteu.api.user.controller;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.application.SaveUserService;
import com.yj.peuteu.api.user.dto.request.SaveUserRequest;
import com.yj.peuteu.api.user.dto.request.ValidatePasswordRequest;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.jwt.domain.UserTokenInfo;
import com.yj.peuteu.common.login.annotation.LoggedIn;
import com.yj.peuteu.common.response.ApiResponse;

import com.yj.peuteu.common.user.UserAssignRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class UserController {

	private final SaveUserService saveUserService;
	private final FindUserService findUserService;
	private final UserAssignRequestService userAssignRequestService;

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

	@GetMapping("/password")
	public ResponseEntity validatePassword(@ModelAttribute ValidatePasswordRequest request, @LoggedIn UserTokenInfo userTokenInfo) {
		userAssignRequestService.assignCurrentUser(request, userTokenInfo);
		return ApiResponse.data(findUserService.validatePassword(request));
	}
}

