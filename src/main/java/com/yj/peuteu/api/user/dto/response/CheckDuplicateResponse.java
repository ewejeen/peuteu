package com.yj.peuteu.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckDuplicateResponse {
    private boolean isDuplicated;
}