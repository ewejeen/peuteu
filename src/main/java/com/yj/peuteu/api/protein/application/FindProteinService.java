package com.yj.peuteu.api.protein.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yj.peuteu.api.protein.domain.Protein;
import com.yj.peuteu.api.protein.dto.request.FindProteinListRequest;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.api.protein.exception.ProteinNotFoundException;
import com.yj.peuteu.api.protein.repository.ProteinJpaRepository;
import com.yj.peuteu.api.protein.repository.ProteinQdslRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindProteinService {
	private final ProteinJpaRepository proteinJpaRepository;
	private final ProteinQdslRepository proteinQdslRepository;

	/**
	 * 해당 날짜의 본인 프로틴 목록 조회
	 * @param request
	 * @param pageable
	 * @return
	 */
	public Page<ProteinListResponse> findMyProteinListByDate(FindProteinListRequest request, Pageable pageable) {

		return proteinQdslRepository.findPageByDate(request, pageable);
	}

	/**
	 * 프로틴 아이디로 상세 조회
	 * @param proteinId
	 * @return
	 */
	public Protein findProteinById(Long proteinId) {
		return proteinJpaRepository.findById(proteinId)
			.orElseThrow(() -> new ProteinNotFoundException());
	}
}
