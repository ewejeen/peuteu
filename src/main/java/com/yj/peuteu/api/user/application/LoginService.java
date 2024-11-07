package com.yj.peuteu.api.user.application;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.dto.request.LoginRequest;
import com.yj.peuteu.api.user.exception.PasswordNotMatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {

	private final FindUserService findUserService;
	private final PasswordEncoder passwordEncoder;

	public void login(LoginRequest request) {
		User user = findUserService.findUserEntityByEmail(request.getEmail());

		if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new PasswordNotMatchException();
		}
		System.out.println("로그인 성공");
	}
}
