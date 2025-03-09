package com.yj.peuteu.api.protein.controller;

import java.time.LocalDate;

import com.yj.peuteu.api.protein.application.CalculateTargetProteinService;
import com.yj.peuteu.api.protein.application.FindProteinService;
import com.yj.peuteu.api.protein.application.SaveProteinService;
import com.yj.peuteu.api.protein.dto.request.*;
import com.yj.peuteu.api.protein.dto.response.ProteinListResponse;
import com.yj.peuteu.common.controller.ApiController;
import com.yj.peuteu.common.jwt.domain.UserTokenInfo;
import com.yj.peuteu.common.login.annotation.LoggedIn;
import com.yj.peuteu.common.response.ApiResponse;
import com.yj.peuteu.common.user.UserAssignRequestService;
import com.yj.peuteu.common.util.LocalDateTimeConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@ApiController
public class ProteinApiController {

    private final SaveProteinService saveProteinService;
    private final FindProteinService findProteinService;
    private final CalculateTargetProteinService calculateTargetProteinService;
    private final UserAssignRequestService userAssignRequestService;

    /**
     * 프로틴 등록
     *
     * @param request
     * @return
     */
    @PostMapping("/protein")
    public ResponseEntity saveProtein(@RequestBody SaveProteinRequest request, @LoggedIn UserTokenInfo userTokenInfo) {
        userAssignRequestService.assignCurrentUser(request, userTokenInfo);
        saveProteinService.saveProtein(request);
        return ApiResponse.created();
    }

    /**
     * 선택한 날짜의 프로틴 기록 목록
     *
     * @param request
     * @param pageable
     * @return
     */
    @GetMapping("/protein")
    public ResponseEntity proteinList(@ModelAttribute FindProteinListRequest request, Pageable pageable, @LoggedIn UserTokenInfo userTokenInfo) {
        userAssignRequestService.assignCurrentUser(request, userTokenInfo);
        Page<ProteinListResponse> page = findProteinService.findMyProteinListByDate(request, pageable);
        return ApiResponse.page(page.getContent(), page.getTotalElements());
    }

    /**
     * 선택한 날짜의 프로틴 섭취량 총합
     *
     * @return
     */
    @GetMapping("/protein-sum")
    public ResponseEntity getProteinSum(@LoggedIn UserTokenInfo userTokenInfo) {
        FindProteinSumOfDayRequest request = FindProteinSumOfDayRequest.builder()
                .userId(userTokenInfo.getId())
                .targetDate(LocalDateTimeConverter.toStringDate(LocalDate.now()))
                .build();
        return ApiResponse.data(findProteinService.findMyProteinSumOfDay(request));
    }

    /**
     * 프로틴 수정
     *
     * @param request
     * @return
     */
    @PatchMapping("/protein")
    public ResponseEntity updateProtein(@RequestBody SaveProteinRequest request) {
        saveProteinService.updateProtein(request);
        return ApiResponse.ok();
    }

    /**
     * 프로틴 삭제
     *
     * @param proteinId
     * @return
     */
    @DeleteMapping("/protein")
    public ResponseEntity deleteProtein(Long proteinId) {
        saveProteinService.deleteProtein(proteinId);
        return ApiResponse.ok();
    }

    /**
     * 프로틴 목표량 조회
     *
     * @return
     */
    @GetMapping("/protein-target")
    public ResponseEntity findMyProteinTarget(@LoggedIn UserTokenInfo userTokenInfo) {
        return ApiResponse.data(findProteinService.findMyProteinTarget(userTokenInfo.getId()));
    }

    /**
     * 프로틴 목표량 수정
     *
     * @param request
     * @return
     */
    @PatchMapping("/protein-target")
    public ResponseEntity updateMyProteinTarget(@RequestBody SaveProteinTargetRequest request, @LoggedIn UserTokenInfo userTokenInfo) {
        userAssignRequestService.assignCurrentUser(request, userTokenInfo);
        saveProteinService.updateProteinTarget(request);
        return ApiResponse.ok();
    }

    /**
     * 선택한 달의 프로틴 목표 달성일 목록
     *
     * @param request
     * @return
     */
    @GetMapping("/protein-month-stat")
    public ResponseEntity findProteinMonthStatList(@ModelAttribute FindProteinMonthStatListRequest request, @LoggedIn UserTokenInfo userTokenInfo) {
        userAssignRequestService.assignCurrentUser(request, userTokenInfo);
        return ApiResponse.data(findProteinService.findProteinMonthStatList(request));
    }

    /**
     * 선택한 달의 프로틴 목표 달성일 카운트
     *
     * @param targetYear
     * @param targetMonth
     * @return
     */
    @GetMapping("/protein-reached-dates-count")
    public ResponseEntity countTargetCompletedDates(int targetYear, int targetMonth, @LoggedIn UserTokenInfo userTokenInfo) {
        return ApiResponse.data(findProteinService.countTargetCompletedDates(userTokenInfo.getId(), targetYear, targetMonth));
    }

    /**
     * 제시된 날짜의 프로틴 섭취량 목록
     *
     * @param request
     * @return
     */
    @GetMapping("/protein-sum-by-dates")
    public ResponseEntity getProteinSumList(@ModelAttribute FindProteinSumListByDatesRequest request, @LoggedIn UserTokenInfo userTokenInfo) {
        userAssignRequestService.assignCurrentUser(request, userTokenInfo);
        return ApiResponse.data(findProteinService.findProteinSumListByDates(request));
    }

    /**
     * 이름으로 음식 검색
     *
     * @param name
     * @return
     */
    @GetMapping("/protein-intake-list")
    public ResponseEntity searchProtein(String name, @LoggedIn UserTokenInfo userTokenInfo) {
        return ApiResponse.data(findProteinService.findProteinInfoByName(userTokenInfo.getId(), name));
    }

    /**
     * 내 목표 섭취량 자동 계산
     *
     * @param userTokenInfo
     * @return
     */
    @GetMapping("/calculate-my-target")
    public ResponseEntity calculateMyTarget(@LoggedIn UserTokenInfo userTokenInfo) {
        return ApiResponse.data(calculateTargetProteinService.calculateMyTargetProtein(userTokenInfo.getId()));
    }
}
