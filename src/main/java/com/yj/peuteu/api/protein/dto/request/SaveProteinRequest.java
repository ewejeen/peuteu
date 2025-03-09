package com.yj.peuteu.api.protein.dto.request;

import com.yj.peuteu.api.user.dto.request.UserAssignRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SaveProteinRequest implements UserAssignRequest {
    private String userId;
    private Long proteinId;
    private String food;
    private Double intake;
    private String intakeTime;

    @Override
    public void assignUserId(String userId) {
        this.userId = userId;
    }
}
