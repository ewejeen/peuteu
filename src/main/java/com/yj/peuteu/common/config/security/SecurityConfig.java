package com.yj.peuteu.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.peuteu.api.user.repository.UserJpaRepository;
import com.yj.peuteu.common.config.jwt.JwtAuthenticationProcessingFilter;
import com.yj.peuteu.common.config.jwt.JwtService;
import com.yj.peuteu.common.config.security.handler.LoginFailureHandler;
import com.yj.peuteu.common.config.security.handler.LoginSuccessJWTProvideHandler;
import com.yj.peuteu.common.config.security.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;
	private final JwtService jwtService;
	private final UserJpaRepository userJpaRepository;

	// 스프링 시큐리티 기능 비활성화 (H2 DB 접근을 위해)
	//	@Bean
	//	public WebSecurityCustomizer configure() {
	//		return (web -> web.ignoring()
	//				.requestMatchers(toH2Console())
	//				.requestMatchers("/h2-console/**")
	//		);
	//	}

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	// 특정 HTTP 요청에 대한 웹 기반 보안 구성
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((authorize) -> authorize
//						.requestMatchers("/api/join", "/", "/api/login").permitAll()
						.requestMatchers("/**").permitAll()
						.anyRequest().authenticated())
				.logout((logout) -> logout
						.logoutSuccessUrl("/login")
						.invalidateHttpSession(true))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);
		http
				.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class) // 추가 : 커스터마이징 된 필터를 SpringSecurityFilterChain에 등록
				.addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);

		http
				.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
				.configurationSource(corsConfigurationSource()));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(List.of("http://localhost:3131"));
		config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	// 인증 관리자 관련 설정
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {//AuthenticationManager 등록
		DaoAuthenticationProvider provider = daoAuthenticationProvider();//DaoAuthenticationProvider 사용
		provider.setPasswordEncoder(passwordEncoder());//PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
		return new ProviderManager(provider);
	}
	@Bean
	public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
		return new LoginSuccessJWTProvideHandler(jwtService, userJpaRepository, objectMapper);
	}

	@Bean
	public LoginFailureHandler loginFailureHandler(){
		return new LoginFailureHandler(objectMapper);
	}
	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return jsonUsernamePasswordLoginFilter;
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
		JwtAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter = new JwtAuthenticationProcessingFilter(jwtService, userJpaRepository);

		return jsonUsernamePasswordLoginFilter;
	}
}
