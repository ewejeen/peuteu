package com.yj.peuteu.api.protein.dto.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProteinSumListByDatesRequest {
    private String userId;
    private List<String> targetDates;

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
