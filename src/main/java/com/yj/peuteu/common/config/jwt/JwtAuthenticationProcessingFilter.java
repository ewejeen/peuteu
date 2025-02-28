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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserJpaRepository userJpaRepository;
	private final List<String> WHITELIST = List.of("/api/login", "/api/join");
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	/**
	 * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
	 * <p>
	 * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (WHITELIST.contains(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		log.info("request: {}", request.getRequestURI());

//		checkAccessTokenAndAuthentication(request, response, filterChain);

		Optional<String> accessToken = jwtService.extractAccessToken(request);
		if(accessToken.isPresent() && jwtService.isTokenValid(accessToken.get())) {
			authenticateUser(accessToken.get());
			filterChain.doFilter(request, response);
			return;
		}

		// Access Token 만료된 경우 Refresh Token 검증 후 재발급
		Optional<String> refreshToken = jwtService.extractRefreshToken(request);
		refreshToken.ifPresent(token -> checkRefreshTokenAndReIssueAccessToken(response, token));

		filterChain.doFilter(request, response);
	}

	// Access Token이 유효한 경우 유저 인증 처리
	private void authenticateUser(String accessToken) {
		jwtService.extractEmail(accessToken)
				.flatMap(userJpaRepository::findByEmail)
				.ifPresent(this::saveAuthentication);
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
		Optional<String> accessToken = jwtService.extractAccessToken(request)
				.filter(jwtService::isTokenValid);

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

	private String reissueRefreshToken(User user) {
		String reissuedRefreshToken = jwtService.createRefreshToken();
		user.updateRefreshToken(reissuedRefreshToken);
		userJpaRepository.saveAndFlush(user);
		return reissuedRefreshToken;
	}

	private void saveAuthentication(User user) {
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 리프레시 토큰 유효 시 액세스 토큰 재발급
	 * @param response
	 * @param refreshToken
	 */
	/*private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		userJpaRepository.findByRefreshToken(refreshToken).ifPresent(
				user -> {
					String newAccessToken = jwtService.createAccessToken(user.getEmail());
					jwtService.sendAccessToken(response, newAccessToken);
				}
		);

	}*/
	/**
	 * Refresh Token이 유효하면 새로운 Access Token을 발급하고, 그렇지 않으면 401 반환
	 */
	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		if (!jwtService.isTokenValid(refreshToken)) {
			log.warn("유효하지 않은 Refresh Token입니다.");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		userJpaRepository.findByRefreshToken(refreshToken)
				.ifPresentOrElse(user -> {
					log.warn("Access Token 재발급.");
					String newAccessToken = jwtService.createAccessToken(user.getEmail());
					jwtService.sendAccessAndRefreshToken(response, newAccessToken, refreshToken);
					authenticateUser(newAccessToken);
				}, () -> {
					log.warn("Refresh Token을 가진 사용자를 찾을 수 없습니다.");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				});
	}
}