package com.yj.peuteu.api.protein.dto.response;

import com.yj.peuteu.common.util.LocalDateTimeConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProteinMonthStatListResponse {
	private final String date;
	private final Double intake;
	private final Double targetIntake;
	private final Boolean isSuccess;

	@Builder
	public ProteinMonthStatListResponse(LocalDate date, Double intake, Double targetIntake, Boolean isSuccess) {
		this.date = LocalDateTimeConverter.toStringDate(date);
		this.intake = intake;
		this.targetIntake = targetIntake;
		this.isSuccess = isSuccess;
	}
}
