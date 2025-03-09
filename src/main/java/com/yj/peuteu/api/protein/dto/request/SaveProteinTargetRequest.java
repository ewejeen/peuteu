package com.yj.peuteu.api.protein.dto.request;

import com.yj.peuteu.api.user.dto.request.UserAssignRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveProteinTargetRequest implements UserAssignRequest {
    private String userId;
    private Double target;

    @Override
    public void assignUserId(String userId) {
        this.userId = userId;
    }
}
