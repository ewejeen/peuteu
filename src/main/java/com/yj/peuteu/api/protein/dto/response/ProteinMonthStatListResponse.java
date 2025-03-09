package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import com.yj.peuteu.common.util.LocalDateTimeConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProteinMonthStatListResponse {
	private final String date;
	private final Double intake;
	private final Double targetIntake;
	private final Boolean isSuccess;

	@QueryProjection
	@Builder
	public ProteinMonthStatListResponse(LocalDateTime date, Double intake, Double targetIntake, Boolean isSuccess) {
		this.date = LocalDateTimeConverter.toStringDate(date);
		this.intake = intake;
		this.targetIntake = targetIntake;
		this.isSuccess = isSuccess;
	}
}
