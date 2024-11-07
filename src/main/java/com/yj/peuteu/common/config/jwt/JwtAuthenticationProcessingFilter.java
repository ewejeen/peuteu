package com.yj.peuteu.common.config.jwt;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.config.security.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserJpaRepository userJpaRepository;
	private final String NO_CHECK_URL = "/api/login";//1
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();//5

	/**
	 * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
	 * <p>
	 * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getRequestURI().equals(NO_CHECK_URL)) {
			filterChain.doFilter(request, response);
			return;//안해주면 아래로 내려가서 계속 필터를 진행하게됨
		}

		String refreshToken = jwtService
				.extractRefreshToken(request)
				.filter(jwtService::isTokenValid)
				.orElse(null); //2

		log.info("refreshToken: {}", refreshToken);

		if (refreshToken != null) {
			log.info("리프레시 토큰 존재");
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);//3
			return;
		}

		log.info("리프레시 토큰 미존재");
		checkAccessTokenAndAuthentication(request, response, filterChain);//4
	}

	/**
	 * 액세스 토큰 체크 및 인증 정보 저장
	 *
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Optional<String> accessToken = jwtService.extractAccessToken(request).filter(jwtService::isTokenValid);

		log.info("accessToken: {}", accessToken);

		accessToken.ifPresent(
				token -> jwtService.extractEmail(token).ifPresent(
						email -> userJpaRepository.findByEmail(email).ifPresent(
								users -> saveAuthentication(users)
						)
				)
		);

		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(User user) {
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

		SecurityContext context = SecurityContextHolder.createEmptyContext();//5
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	/**
	 * 리프레시 토큰 유효 시 액세스 토큰 재발급
	 * @param response
	 * @param refreshToken
	 */
	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		userJpaRepository.findByRefreshToken(refreshToken).ifPresent(
				users -> jwtService.sendAccessToken(response, jwtService.createAccessToken(users.getEmail()))
		);

	}
}