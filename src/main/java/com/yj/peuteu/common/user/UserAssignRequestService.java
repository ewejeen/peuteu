package com.yj.peuteu.common.user;

import com.yj.peuteu.api.user.dto.request.UserAssignRequest;
import com.yj.peuteu.common.jwt.domain.UserTokenInfo;
import org.springframework.stereotype.Service;

@Service
public class UserAssignRequestService {
    /**
     * 컨트롤러에서 반복되는 setUserId를 제거
     * - UserRequest를 구현한 클래스라면 어떤 타입이든 받을 수 있음
     *
     * @param request
     * @param userTokenInfo
     * @param <T>
     */
    public <T extends UserAssignRequest> void assignCurrentUser(T request, UserTokenInfo userTokenInfo) {
        request.assignUserId(userTokenInfo.getId());
    }
}
