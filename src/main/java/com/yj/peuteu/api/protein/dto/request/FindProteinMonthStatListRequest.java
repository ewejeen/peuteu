package com.yj.peuteu.api.protein.dto.request;

import com.yj.peuteu.api.user.dto.request.UserAssignRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProteinMonthStatListRequest implements UserAssignRequest {
    private String userId;
    private int targetYear;
    private int targetMonth;

    @Override
    public void assignUserId(String userId) {
        this.userId = userId;
    }
}
