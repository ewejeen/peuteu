package com.yj.peuteu.common.config.security.user;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserJpaRepository userJpaRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userJpaRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException(email));
		return new UserDetailsImpl(user);
	}
}
