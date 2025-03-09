package com.yj.peuteu.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.common.jwt.filter.JwtAuthenticationFilter;
import com.yj.peuteu.common.jwt.service.BlacklistedTokenService;
import com.yj.peuteu.common.jwt.service.RefreshTokenService;
import com.yj.peuteu.common.jwt.util.JwtUtil;
import com.yj.peuteu.common.security.filter.JsonAuthenticationProcessingFilter;
import com.yj.peuteu.common.security.handler.LoginFailureHandler;
import com.yj.peuteu.common.security.handler.LoginSuccessHandler;
import com.yj.peuteu.common.security.user.UserDetailsServiceImpl;
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

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;
    private final FindUserService findUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/join", "/api/login", "/api/logout", "/api/refresh", "/error").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 필터 설정
        http
                .addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), JsonAuthenticationProcessingFilter.class);

        // CORS 설정
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3131", "http://192.168.45.132:3131", "http://192.168.45.90:3131", "http://221.140.87.2:3131"));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 비밀번호 암호화
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 스프링 시큐리티 인증 관련 설정
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    // 로그인 성공 핸들러
    @Bean
    public LoginSuccessHandler loginSuccessJWTProvideHandler() {
        return new LoginSuccessHandler(jwtUtil, findUserService, refreshTokenService, objectMapper);
    }

    // 로그인 실패 핸들러
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    // 로그인 처리 필터 및 성공, 실패 핸들러 설정
    @Bean
    public JsonAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter() {
        JsonAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter = new JsonAuthenticationProcessingFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordLoginFilter;
    }

    // 매 요청 시 권한 인증 필터
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, findUserService, blacklistedTokenService);
    }
}
