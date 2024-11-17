package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProteinMonthStatListResponse {
	private String date;
	private double intake;
	private double targetIntake;
	private boolean isSuccess;

	@QueryProjection
	@Builder
	public ProteinMonthStatListResponse(String date, double intake, double targetIntake, boolean isSuccess) {
		this.date = date;
		this.intake = intake;
		this.targetIntake = targetIntake;
		this.isSuccess = isSuccess;
	}
}
