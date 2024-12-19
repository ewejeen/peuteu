package com.yj.peuteu.api.protein.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveProteinTargetRequest {
    private String userId;
    private Double target;
}
