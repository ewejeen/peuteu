package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.yj.peuteu.common.util.LocalDateTimeConverter;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ProteinListResponse {
	private Long id;
	private String name;
	private double intake;
	private String intakeTime;

	@QueryProjection
	public ProteinListResponse(Long id, String name, double intake, LocalDateTime intakeTime) {
		this.id = id;
		this.name = name;
		this.intake = intake;
		this.intakeTime = LocalDateTimeConverter.toStringDateTime(intakeTime);
	}
}
