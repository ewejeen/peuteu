package com.yj.peuteu.api.protein.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yj.peuteu.api.protein.domain.QTargetIntake;
import com.yj.peuteu.api.protein.dto.request.FindProteinListRequest;
import com.yj.peuteu.api.protein.dto.request.FindProteinMonthStatListRequest;
import com.yj.peuteu.api.protein.dto.request.FindProteinSumListByDatesRequest;
import com.yj.peuteu.api.protein.dto.request.FindProteinSumOfDayRequest;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.api.protein.dto.response.ProteinMonthStatListResponse;
import com.yj.peuteu.api.protein.dto.response.ProteinSearchListResponse;
import com.yj.peuteu.api.protein.dto.response.ProteinSumListByDatesResponse;
import com.yj.peuteu.api.protein.dto.response.QProteinListResponse;
import com.yj.peuteu.api.protein.dto.response.QProteinSearchListResponse;
import com.yj.peuteu.api.protein.dto.response.QProteinSumListByDatesResponse;
import com.yj.peuteu.common.enums.DeleteYn;
import com.yj.peuteu.common.util.LocalDateTimeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static com.yj.peuteu.api.protein.domain.QProtein.protein;
import static com.yj.peuteu.api.protein.domain.QTargetIntake.targetIntake;
import static com.yj.peuteu.api.user.domain.QUser.user;

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

	public Double findMyProteinSumOfDay(FindProteinSumOfDayRequest request) {
		return queryFactory
				.select(
						protein.intake.sum()
				)
				.from(protein)
				.leftJoin(protein.user, user)
				.where(protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)),
						user.id.eq(request.getUserId()),
						eqTargetDate(protein.intakeTime, request.getTargetDate())
				)
				.fetchOne();
	}

	public Double findMyProteinTarget(String userId) {
		QTargetIntake subIntake = new QTargetIntake("sub");
		return queryFactory
				.select(targetIntake.target)
				.from(targetIntake)
				.leftJoin(targetIntake.user, user)
				.where(
						user.id.eq(userId),
						targetIntake.createdAt.eq(
						JPAExpressions.select(subIntake.createdAt.max())
								.from(subIntake)
								.where(subIntake.createdAt.loe(LocalDateTime.now()))
								.orderBy(subIntake.createdAt.desc())
				))
				.fetchOne();

	}

	public List<ProteinMonthStatListResponse> findProteinMonthStatList(FindProteinMonthStatListRequest request) {
		LocalDateTime firstTimeOfMonth = getFirstTimeOfMonth(request.getTargetYear(), request.getTargetMonth());
		LocalDateTime lastTimeOfMonth = getLastTimeOfMonth(request.getTargetYear(), request.getTargetMonth());

		QTargetIntake subIntake = new QTargetIntake("sub");

		List<Tuple> result = queryFactory
			.select(
				protein.intakeTime,
				protein.intake.sum(),
				JPAExpressions.select(targetIntake.target)
					.from(targetIntake)
					.where(targetIntake.createdAt.eq(
						JPAExpressions.select(subIntake.createdAt.max())
							.from(subIntake)
							.where(subIntake.createdAt.loe(protein.intakeTime))
							.orderBy(subIntake.createdAt.desc())
					)),
				protein.intake.sum().goe(
					JPAExpressions.select(targetIntake.target)
						.from(targetIntake)
						.where(targetIntake.createdAt.eq(
							JPAExpressions.select(subIntake.createdAt.max())
								.from(subIntake)
								.where(subIntake.createdAt.loe(protein.intakeTime))
								.orderBy(subIntake.createdAt.desc())
						))
				).coalesce(false)
			)
			.from(protein)
			.leftJoin(protein.user, user)
			.where(
				user.id.eq(request.getUserId())
					.and(protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)))
					.and(protein.intakeTime.between(firstTimeOfMonth, lastTimeOfMonth))
			)
			.groupBy(
					protein.intakeTime.year(),
					protein.intakeTime.month(),
					protein.intakeTime.dayOfMonth()
			)
			.fetch();

		// 목표를 달성한 날짜만 필터링하여 목록 생성
		return result.stream()
			// .filter(tuple -> tuple.get(3, Boolean.class))
			.map(tuple -> ProteinMonthStatListResponse.builder()
				.date(tuple.get(0, LocalDateTime.class))
				.intake(tuple.get(1, Double.class))
				.targetIntake(tuple.get(2, Double.class))
				.isSuccess(tuple.get(3, Boolean.class))
				.build())
			.collect(Collectors.toList());
	}

	public Integer countTargetCompletedDates(String userId, int targetYear, int targetMonth) {
		LocalDateTime firstTimeOfMonth = getFirstTimeOfMonth(targetYear, targetMonth);
		LocalDateTime lastTimeOfMonth = getLastTimeOfMonth(targetYear, targetMonth);

		QTargetIntake subIntake = new QTargetIntake("sub");

		List<Boolean> targetReachedList = queryFactory
				.select(
						protein.intake.sum().goe(
								JPAExpressions.select(targetIntake.target)
										.from(targetIntake)
										.where(targetIntake.createdAt.eq(
												JPAExpressions.select(subIntake.createdAt.max())
														.from(subIntake)
														.where(subIntake.createdAt.loe(protein.intakeTime))
														.orderBy(subIntake.createdAt.desc())
										))
						)
				)
				.from(protein)
				.leftJoin(protein.user, user)
				.where(
						user.id.eq(userId)
								.and(protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)))
								.and(protein.intakeTime.between(firstTimeOfMonth, lastTimeOfMonth))
				)
				.groupBy(
						user.id,
						protein.intakeTime.year(),
						protein.intakeTime.month(),
						protein.intakeTime.dayOfMonth()
				)
				.fetch();

		return targetReachedList.stream()
				.mapToInt(reached -> reached ? 1 : 0)
				.sum();
	}

	public List<ProteinSumListByDatesResponse> findProteinSumListByDates(FindProteinSumListByDatesRequest request) {
		return queryFactory
			.select(
				new QProteinSumListByDatesResponse(
					protein.intakeTime,
					protein.intake.sum()
				)
			)
			.from(protein)
			.leftJoin(protein.user, user)
			.where(protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)),
				user.id.eq(request.getUserId()),
				inTargetDate(protein.intakeTime, request.getTargetDates())
			)
			.groupBy(
					protein.intakeTime.year(),
					protein.intakeTime.month(),
					protein.intakeTime.dayOfMonth()
			)
			.orderBy(protein.intakeTime.asc())
			.fetch();
	}

	public List<ProteinSearchListResponse> findProteinInfoByName(String userId, String name) {
		return queryFactory
			.select(
				new QProteinSearchListResponse(
					protein.food,
					protein.intake
				)
			)
			.from(protein)
			.leftJoin(protein.user, user)
			.where(
				protein.deleteYn.isNull().or(protein.deleteYn.eq(DeleteYn.N)),
				user.id.eq(userId),
				name == null ? null : protein.food.contains(name)
			)
			.orderBy(protein.food.asc())
			.distinct()
			.fetch();
	}

	private BooleanExpression eqTargetDate(DateTimePath<LocalDateTime> intakeTime, String targetDate) {
		if (targetDate == null) {
			return null;
		}
		return intakeTime.loe(LocalDateTimeConverter.toLocalDateTime(targetDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"))
			.and(intakeTime.goe(LocalDateTimeConverter.toLocalDateTime(targetDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss")));
	}

	private BooleanExpression inTargetDate(DateTimePath<LocalDateTime> intakeTime, List<String> targetDates) {
		if (targetDates == null) {
			return null;
		}

		// yyyy-MM-dd 형식의 String으로 변환
		StringExpression stringIntakeTime = intakeTime.year().stringValue()
				.concat("-")
				.concat(new CaseBuilder()
						.when(intakeTime.month().lt(10)) // 한 자리 수 월 처리
						.then("0")
						.otherwise(""))
				.concat(intakeTime.month().stringValue())
				.concat("-")
				.concat(new CaseBuilder()
						.when(intakeTime.dayOfMonth().lt(10)) // 한 자리 수 일 처리
						.then("0")
						.otherwise(""))
				.concat(intakeTime.dayOfMonth().stringValue());

		return stringIntakeTime.in(targetDates);
	}

	private LocalDateTime getFirstTimeOfMonth(int targetYear, int targetMonth) {
		return LocalDate.of(targetYear, targetMonth, 1).atTime(0, 0, 0);
	}

	private LocalDateTime getLastTimeOfMonth(int targetYear, int targetMonth) {
		LocalDate lastDateOfMonth = LocalDate.of(targetYear, targetMonth, 1).with(TemporalAdjusters.lastDayOfMonth());
		return lastDateOfMonth.atTime(23, 59, 59);
	}
}
