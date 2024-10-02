package com.yj.peuteu.api.user.application;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.dto.request.FindUserResponse;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindUserService {
    private final UserJpaRepository userJpaRepository;

    public FindUserResponse findUser(String userId) {
        User user = findUserEntity(userId);
        return FindUserResponse.from(user);
    }

    public User findUserEntity(String userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다"));
    }
}
