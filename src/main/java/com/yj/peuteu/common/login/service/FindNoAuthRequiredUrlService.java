package com.yj.peuteu.common.login.service;

import com.yj.peuteu.common.login.annotation.NoAuthRequired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FindNoAuthRequiredUrlService {
    private final RequestMappingHandlerMapping mapping;

    public FindNoAuthRequiredUrlService(ApplicationContext applicationContext) {
        this.mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
    }

    /**
     * @return
     * @NoAuthRequired 어노테이션이 붙은 URL 목록 찾기
     * - 해당 URL은 권한 미체크 (로그아웃 상태에서도 이용 가능)
     */
    public List<String> getNoAuthRequiredUrls() {
        List<String> urls = mapping.getHandlerMethods().entrySet().stream()
                .filter(entry -> entry.getValue().hasMethodAnnotation(NoAuthRequired.class))
                .map(entry -> {
                    Set<PathPattern> patterns = entry.getKey().getPathPatternsCondition().getPatterns();
                    return patterns.isEmpty() ? null : patterns.iterator().next().toString();
                })
                .filter(url -> url != null)
                .collect(Collectors.toList());

        // 로그인 API는 시큐리티에서 처리하므로 별도 추가
        urls.add("/api/login");

        return urls;
    }
}
