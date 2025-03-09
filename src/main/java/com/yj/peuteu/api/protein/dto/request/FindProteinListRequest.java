package com.yj.peuteu.api.protein.dto.request;

import com.yj.peuteu.api.user.dto.request.UserAssignRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class FindProteinListRequest implements UserAssignRequest {
    @Setter
    private String userId;
    private String targetDate;

    @Override
    public void assignUserId(String userId) {
        this.userId = userId;
    }
}
