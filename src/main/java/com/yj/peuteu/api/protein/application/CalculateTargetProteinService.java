package com.yj.peuteu.api.protein.application;

import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.domain.Goal;
import com.yj.peuteu.api.user.dto.response.FindUserResponse;
import com.yj.peuteu.common.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CalculateTargetProteinService {
    private final FindUserService findUserService;

    /**
     * 내 목표 섭취량 자동 계산
     *
     * @param userId
     * @return
     */
    public Double calculateMyTargetProtein(String userId) {
        FindUserResponse user = findUserService.findUserById(userId);

        Double weight = user.getWeight();
        Goal goal = user.getGoal();

        if (weight == null) {
            throw new ApplicationException("몸무게 정보가 없습니다. 회원 정보에서 등록해 주세요.");
        }
        if (goal == null) {
            throw new ApplicationException("섭취 목적 정보가 없습니다. 회원 정보에서 등록해 주세요.");
        }

        return calculateTarget(goal, weight);
    }

    private Double calculateTarget(Goal goal, Double weight) {
        // 건강 = 1배, 근성장 = 1.5배
        Double standard = 0.0;
        if (goal.equals(Goal.HEALTH)) {
            standard = 1.0;
        } else if (goal.equals(Goal.MUSCLE)) {
            standard = 1.5;
        }

        return weight * standard;
    }
}
