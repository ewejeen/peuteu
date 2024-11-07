package com.yj.peuteu.api.user.application;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.dto.request.FindUserResponse;
import com.yj.peuteu.api.user.exception.UserNotFoundException;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FindUserService {
    private final UserJpaRepository userJpaRepository;

    public FindUserResponse findUserById(String userId) {
        User user = findUserEntity(userId);
        return FindUserResponse.from(user);
    }

    /*public FindUserResponse findUserByEmail(String email) {
        User user = findUserEntityByEmail(email);
        return FindUserResponse.from(user);
    }*/

    public List<FindUserResponse> findAllUsers() {
        List<User> users = userJpaRepository.findAll();
        return users.stream().map(FindUserResponse::from).collect(Collectors.toList());
    }

    public User findUserEntity(String userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
    }

    public User findUserEntityByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());
    }
}
