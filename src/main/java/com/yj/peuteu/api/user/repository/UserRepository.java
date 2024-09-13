package com.yj.peuteu.api.user.repository;

import com.yj.peuteu.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}