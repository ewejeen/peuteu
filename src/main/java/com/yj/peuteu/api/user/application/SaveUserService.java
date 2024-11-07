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

	public String saveUser(SaveUserRequest request) {
		User user = User.builder()
				.id(CodeGenerator.generateCode())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.nickname(request.getNickname())
				.gender(Gender.ofCode(request.getGender()))
				.height(request.getHeight())
				.weight(request.getWeight())
				.goal(Goal.ofCode(request.getGoal()))
				.build();

		return userJpaRepository.save(user).getId();
	}
}
