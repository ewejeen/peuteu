package com.yj.peuteu.api.protein.application;

import java.util.List;

import com.yj.peuteu.api.protein.domain.Protein;
import com.yj.peuteu.api.protein.dto.request.FindProteinListRequest;
import com.yj.peuteu.api.protein.dto.request.FindProteinSumListByDatesRequest;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.api.protein.dto.response.ProteinMonthStatListResponse;
import com.yj.peuteu.api.protein.dto.response.ProteinSearchListResponse;
import com.yj.peuteu.api.protein.dto.response.ProteinSumListByDatesResponse;
import com.yj.peuteu.api.protein.exception.ProteinNotFoundException;
import com.yj.peuteu.api.protein.repository.ProteinJpaRepository;
import com.yj.peuteu.api.protein.repository.ProteinQdslRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindProteinService {
	private final ProteinJpaRepository proteinJpaRepository;
	private final ProteinQdslRepository proteinQdslRepository;

	/**
	 * 해당 날짜의 본인 프로틴 목록 조회
	 *
	 * @param request
	 * @param pageable
	 * @return
	 */
	public Page<ProteinListResponse> findMyProteinListByDate(FindProteinListRequest request, Pageable pageable) {

		return proteinQdslRepository.findPageByDate(request, pageable);
	}

	/**
	 * 해당 날짜의 본인 프로틴 총합 조회
	 *
	 * @param targetDate
	 * @return
	 */
	public Double findMyProteinSumOfDay(String targetDate) {
		String userId = "somxkosub2no";
		return proteinQdslRepository.findMyProteinSumOfDay(userId, targetDate);
	}

	/**
	 * 프로틴 아이디로 상세 조회
	 *
	 * @param proteinId
	 * @return
	 */
	public Protein findProteinById(Long proteinId) {
		return proteinJpaRepository.findById(proteinId)
			.orElseThrow(() -> new ProteinNotFoundException());
	}

	/**
	 * 현재 내 프로틴 섭취 목표량 조회
	 *
	 * @return
	 */
	public Double findMyProteinTarget() {
		String userId = "somxkosub2no";
		return proteinQdslRepository.findMyProteinTarget(userId);
	}

	/**
	 * 선택한 달의 프로틴 목표 달성일 목록
	 *
	 * @param targetYear
	 * @param targetMonth
	 * @return
	 */
	public List<ProteinMonthStatListResponse> findProteinMonthStatList(int targetYear, int targetMonth) {
		String userId = "somxkosub2no";
		return proteinQdslRepository.findProteinMonthStatList(userId, targetYear, targetMonth);
	}

	/**
	 * 선택한 달의 프로틴 목표 달성일 카운트
	 *
	 * @param targetYear
	 * @param targetMonth
	 * @return
	 */
	public Integer countTargetCompletedDates(int targetYear, int targetMonth) {
		String userId = "somxkosub2no";
		return proteinQdslRepository.countTargetCompletedDates(userId, targetYear, targetMonth);
	}

	/**
	 * 제시된 날짜의 프로틴 섭취량 목록
	 *
	 * @param request
	 * @return
	 */
	public List<ProteinSumListByDatesResponse> findProteinSumListByDates(FindProteinSumListByDatesRequest request) {
		String userId = "somxkosub2no";
		request.setUserId(userId);
		return proteinQdslRepository.findProteinSumListByDates(request);
	}

	/**
	 * 이름으로 음식 검색
	 *
	 * @param name
	 * @return
	 */
	public List<ProteinSearchListResponse> findProteinInfoByName(String name) {
		String userId = "somxkosub2no";
		return proteinQdslRepository.findProteinInfoByName(userId, name);
	}
}
