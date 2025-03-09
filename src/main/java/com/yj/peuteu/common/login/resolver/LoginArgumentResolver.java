package com.yj.peuteu.common.login.resolver;

import com.yj.peuteu.common.jwt.util.JwtUtil;
import com.yj.peuteu.common.login.annotation.LoggedIn;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@AllArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtil jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoggedIn.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) return null;

        return jwtService.extractAccessTokenFromHeader(request)
                .map(jwtService::extractUserInfoFromAccessToken).
                orElseThrow(() -> new RuntimeException("No access token found"));
    }
}
