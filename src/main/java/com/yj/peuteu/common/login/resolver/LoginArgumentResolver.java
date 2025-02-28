package com.yj.peuteu.common.login.resolver;

import com.yj.peuteu.common.jwt.util.JwtUtil;
import com.yj.peuteu.common.login.annotation.LoggedIn;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@AllArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtUtil jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoggedIn.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        HttpServletRequeessst request = webRequest.getNativeRequest(HttpServletRequest.class);
////		return tokenUtil.getUserSion(request);
        return null;
    }
}
