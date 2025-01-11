package com.yj.peuteu.api.user.application;

import com.yj.peuteu.api.user.domain.Gender;
import com.yj.peuteu.api.user.domain.Goal;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.dto.request.SaveUserRequest;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.util.CodeGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SaveUserService {
	private final PasswordEncoder passwordEncoder;

	private final UserJpaRepository userJpaRepository;
	private final FindUserService findUserService;

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	public String saveUser(SaveUserRequest request) {
		User user = User.builder()
			.id(CodeGenerator.generateCode())
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.nickname(request.getNickname())
			.gender(Gender.ofValue(request.getGender()))
			.height(request.getHeight())
			.weight(request.getWeight())
			.goal(Goal.ofValue(request.getGoal()))
			.build();

		return userJpaRepository.save(user).getId();
	}

	/**
	 * 회원가입
	 * @param request
	 * @return
	 */
	public void updateUser(SaveUserRequest request) {
		User user = findUserService.findUserEntity(request.getId());
		user.update(request);
	}
}
