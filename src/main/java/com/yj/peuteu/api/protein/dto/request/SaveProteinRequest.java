package com.yj.peuteu.api.protein.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveProteinRequest {
    private String userId;
    private String food;
    private Double intake;
    private String intakeTime;
}
