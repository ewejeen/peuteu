package com.yj.peuteu.api.user.application;

import com.yj.peuteu.api.user.domain.Gender;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.dto.request.SaveUserRequest;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class SaveUserService {
    private final UserJpaRepository userJpaRepository;

    public void saveUser(SaveUserRequest request) {
        User user = User.builder()
                .idx(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .gender(Gender.ofCode(request.getGender()))
                .height(request.getHeight())
                .weight(request.getWeight())
                .goal(request.getGoal())
                .build();

        userJpaRepository.save(user);
    }
}
