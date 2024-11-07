package com.yj.peuteu.api.protein.repository;

import static com.yj.peuteu.api.protein.domain.QProtein.*;
import static com.yj.peuteu.api.user.domain.QUser.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yj.peuteu.api.protein.dto.request.FindProteinListRequest;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.api.protein.dto.response.QProteinListResponse;
import com.yj.peuteu.common.enums.DeleteYn;
import com.yj.peuteu.common.util.LocalDateTimeConverter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProteinQdslRepository {
	private final JPAQueryFactory queryFactory;

	public Page<ProteinListResponse> findPageByDate(FindProteinListRequest request, Pageable pageable) {
		List<ProteinListResponse> list = queryFactory
			.select(
				new QProteinListResponse(
					protein.id,
					protein.food,
					protein.intake,
					protein.intakeTime
				)
			)
			.from(protein)
			.leftJoin(protein.user, user)
			.where(
				protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)),
				user.id.eq(request.getUserId()),
				eqTargetDate(protein.intakeTime, request.getTargetDate())
			)
			.orderBy(
				protein.intakeTime.desc()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(protein.count())
			.from(protein)
			.leftJoin(protein.user, user)
			.where(
				protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)),
				user.id.eq(request.getUserId()),
				eqTargetDate(protein.intakeTime, request.getTargetDate())
			)
			.fetchOne();

		return new PageImpl<>(list, pageable, count);
	}

	private BooleanExpression eqTargetDate(DateTimePath<LocalDateTime> intakeTime, String targetDate) {
		if (targetDate == null) {
			return null;
		}
		return intakeTime.loe(LocalDateTimeConverter.toLocalDateTime(targetDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"))
			.and(intakeTime.goe(LocalDateTimeConverter.toLocalDateTime(targetDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss")));
	}

	public Double findMyProteinSumOfDay(String userId, String targetDate) {
		return queryFactory
			.select(
				protein.intake.sum()
			)
			.from(protein)
			.where(protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)),
				user.id.eq(userId),
				eqTargetDate(protein.intakeTime, targetDate)
			)
			.fetchOne();
	}
}
