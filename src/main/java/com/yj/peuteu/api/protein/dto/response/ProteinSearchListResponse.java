package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ProteinSearchListResponse {
	private String name;
	private double intake;

	@QueryProjection
	public ProteinSearchListResponse(String name, double intake) {
		this.name = name;
		this.intake = intake;
	}
}
