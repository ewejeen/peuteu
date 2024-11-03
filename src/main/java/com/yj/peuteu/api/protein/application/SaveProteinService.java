package com.yj.peuteu.api.protein.application;

import com.yj.peuteu.api.protein.domain.Protein;
import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.api.protein.repository.ProteinJpaRepository;
import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.util.LocalDateTimeConverter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SaveProteinService {
	private final ProteinJpaRepository proteinJpaRepository;
	private final FindProteinService findProteinService;
	private final FindUserService findUserService;

	/**
	 * 프로틴 등록
	 * @param request
	 * @return
	 */
	public Protein saveProtein(SaveProteinRequest request) {
		User user = findUserService.findUserEntity(request.getUserId());
		Protein protein = Protein.builder()
			.user(user)
			.food(request.getFood())
			.intake(request.getIntake())
			.intakeTime(LocalDateTimeConverter.toLocalDateTimeSecond(request.getIntakeTime()))
			.build();

		return proteinJpaRepository.save(protein);
	}

	/**
	 * 프로틴 수정
	 * @param request
	 * @return
	 */
	public Protein updateProtein(SaveProteinRequest request) {
		Protein protein = findProteinService.findProteinById(request.getProteinId());
		return protein.update(request);
	}

	/**
	 * 프로틴 삭제
	 * @param proteinId
	 */
	public void deleteProtein(Long proteinId) {
		Protein protein = findProteinService.findProteinById(proteinId);
		protein.delete();
	}
}
