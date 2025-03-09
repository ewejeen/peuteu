package com.yj.peuteu.api.protein.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import com.yj.peuteu.common.util.LocalDateTimeConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProteinSumListByDatesResponse {
	private String date;
	private Double sum;

	@Builder
	@QueryProjection
	public ProteinSumListByDatesResponse(LocalDateTime date, Double sum) {
		this.date = LocalDateTimeConverter.toStringDate(date);
		this.sum = sum;
	}
}
