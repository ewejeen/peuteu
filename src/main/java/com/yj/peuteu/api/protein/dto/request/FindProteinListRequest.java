package com.yj.peuteu.api.protein.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProteinListRequest {
    private String userId;
    private String targetDate;

}
