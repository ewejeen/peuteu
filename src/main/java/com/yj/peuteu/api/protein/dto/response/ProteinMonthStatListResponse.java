package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import com.yj.peuteu.common.util.LocalDateTimeConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProteinMonthStatListResponse {
	private String date;
	private double intake;
	private double targetIntake;
	private boolean isSuccess;

	@QueryProjection
	@Builder
	public ProteinMonthStatListResponse(LocalDateTime date, double intake, double targetIntake, boolean isSuccess) {
		this.date = LocalDateTimeConverter.toStringDate(date);
		this.intake = intake;
		this.targetIntake = targetIntake;
		this.isSuccess = isSuccess;
	}
}
