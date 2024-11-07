package com.yj.peuteu.common.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.peuteu.api.user.dto.response.LoginResponse;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.config.jwt.JwtService;
import com.yj.peuteu.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final UserJpaRepository userJpaRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String email = extractEmail(authentication);
		String accessToken = jwtService.createAccessToken(email);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		userJpaRepository.findByEmail(email).ifPresent(
				users -> users.updateRefreshToken(refreshToken)
		);

		log.info("로그인에 성공합니다. email: {}", email);
		log.info("AccessToken 을 발급합니다. AccessToken: {}", accessToken);
		log.info("RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		LoginResponse loginResponse = LoginResponse.builder()
				.email(email)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
	}

	private String extractEmail(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		return userDetails.getUsername();
	}
}