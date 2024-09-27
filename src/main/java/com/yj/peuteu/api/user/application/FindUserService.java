package com.yj.peuteu.api.user.application;

import com.yj.peuteu.api.user.domain.Gender;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.dto.request.FindUserResponse;
import com.yj.peuteu.api.user.dto.request.SaveUserRequest;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FindUserService {
    private final UserJpaRepository userJpaRepository;

    public FindUserResponse findUser(String userIdx) {
        User user = userJpaRepository.findById(userIdx)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다"));

        return FindUserResponse.from(user);
    }
}
