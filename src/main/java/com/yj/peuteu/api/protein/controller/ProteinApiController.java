package com.yj.peuteu.api.protein.controller;

import com.yj.peuteu.api.protein.application.FindProteinService;
import com.yj.peuteu.api.protein.application.SaveProteinService;
import com.yj.peuteu.api.protein.dto.request.FindProteinListRequest;
import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class ProteinApiController {

	private final SaveProteinService saveProteinService;
	private final FindProteinService findProteinService;

	@PostMapping("/protein")
	public ResponseEntity saveProtein(@RequestBody SaveProteinRequest request) {
		//test
		request.setUserId("somxkosub2no");
		saveProteinService.saveProtein(request);
		return ApiResponse.created();
	}

	@GetMapping("/protein")
	public ResponseEntity proteinList(@ModelAttribute FindProteinListRequest request, Pageable pageable) {
		Page<ProteinListResponse> page = findProteinService.findMyProteinListByDate(request, pageable);
		return ApiResponse.page(page.getContent(), page.getTotalElements());
	}

	@PatchMapping("/protein")
	public ResponseEntity updateProtein(@RequestBody SaveProteinRequest request) {
		saveProteinService.updateProtein(request);
		return ApiResponse.ok();
	}

	@DeleteMapping("/protein")
	public ResponseEntity deleteProtein(Long proteinId) {
		saveProteinService.deleteProtein(proteinId);
		return ApiResponse.ok();
	}
}

