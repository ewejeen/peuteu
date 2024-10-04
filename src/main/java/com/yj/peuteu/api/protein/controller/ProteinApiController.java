package com.yj.peuteu.api.protein.controller;

import com.yj.peuteu.api.protein.application.SaveProteinService;
import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class ProteinApiController {

    private final SaveProteinService saveProteinService;

    @PostMapping("/protein")
    public ResponseEntity saveProtein(@RequestBody SaveProteinRequest request) {
        saveProteinService.saveProtein(request);
        return ApiResponse.created();
    }
}

