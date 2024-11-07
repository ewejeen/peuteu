package com.yj.peuteu.api.user.repository;

import com.yj.peuteu.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, String> {
	Optional<User> findByEmail(String email);
	Optional<User> findByRefreshToken(String refreshToken);

}