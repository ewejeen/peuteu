package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProteinSumListByDatesResponse {
	private String date;
	private Double sum;

	@Builder
	@QueryProjection
	public ProteinSumListByDatesResponse(String date, Double sum) {
		this.date = date;
		this.sum = sum;
	}
}
