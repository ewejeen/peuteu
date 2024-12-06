package com.yj.peuteu.api.protein.controller;

import java.time.LocalDate;

import com.yj.peuteu.api.protein.application.FindProteinService;
import com.yj.peuteu.api.protein.application.SaveProteinService;
import com.yj.peuteu.api.protein.dto.request.FindProteinListRequest;
import com.yj.peuteu.api.protein.dto.request.FindProteinSumListByDatesRequest;
import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.response.ApiResponse;
import com.yj.peuteu.common.util.LocalDateTimeConverter;

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

	/**
	 * 프로틴 등록
	 * @param request
	 * @return
	 */
	@PostMapping("/protein")
	public ResponseEntity saveProtein(@RequestBody SaveProteinRequest request) {
		//test
		request.setUserId("somxkosub2no");
		saveProteinService.saveProtein(request);
		return ApiResponse.created();
	}

	/**
	 * 선택한 날짜의 프로틴 기록 목록
	 * @param request
	 * @param pageable
	 * @return
	 */
	@GetMapping("/protein")
	public ResponseEntity proteinList(@ModelAttribute FindProteinListRequest request, Pageable pageable) {
		Page<ProteinListResponse> page = findProteinService.findMyProteinListByDate(request, pageable);
		return ApiResponse.page(page.getContent(), page.getTotalElements());
	}

	/**
	 * 선택한 날짜의 프로틴 섭취량 총합
	 * @return
	 */
	@GetMapping("/protein-sum")
	public ResponseEntity getProteinSum() {
		String targetDate = LocalDateTimeConverter.toStringDate(LocalDate.now());
		return ApiResponse.data(findProteinService.findMyProteinSumOfDay(targetDate));
	}

	/**
	 * 프로틴 수정
	 * @param request
	 * @return
	 */
	@PatchMapping("/protein")
	public ResponseEntity updateProtein(@RequestBody SaveProteinRequest request) {
		saveProteinService.updateProtein(request);
		return ApiResponse.ok();
	}

	/**
	 * 프로틴 삭제
	 * @param proteinId
	 * @return
	 */
	@DeleteMapping("/protein")
	public ResponseEntity deleteProtein(Long proteinId) {
		saveProteinService.deleteProtein(proteinId);
		return ApiResponse.ok();
	}

	/**
	 * 프로틴 목표량 조회
	 * @return
	 */
	@GetMapping("/protein-target")
	public ResponseEntity findMyProteinTarget() {
		return ApiResponse.data(findProteinService.findMyProteinTarget());
	}

	/**
	 * 선택한 달의 프로틴 목표 달성일 목록
	 * @param targetYear
	 * @param targetMonth
	 * @return
	 */
	@GetMapping("/protein-month-stat")
	public ResponseEntity findProteinMonthStatList(int targetYear, int targetMonth) {
		return ApiResponse.data(findProteinService.findProteinMonthStatList(targetYear, targetMonth));
	}

	/**
	 * 선택한 달의 프로틴 목표 달성일 카운트
	 * @param targetYear
	 * @param targetMonth
	 * @return
	 */
	@GetMapping("/protein-reached-dates-count")
	public ResponseEntity countTargetCompletedDates(int targetYear, int targetMonth) {
		return ApiResponse.data(findProteinService.countTargetCompletedDates(targetYear, targetMonth));
	}

	/**
	 * 제시된 날짜의 프로틴 섭취량 목록
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/protein-sum-by-dates")
	public ResponseEntity getProteinSumList(@ModelAttribute FindProteinSumListByDatesRequest request) {
		return ApiResponse.data(findProteinService.findProteinSumListByDates(request));
	}
}

