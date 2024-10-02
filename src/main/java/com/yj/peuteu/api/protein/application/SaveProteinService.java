package com.yj.peuteu.api.protein.application;

import com.yj.peuteu.api.protein.domain.Protein;
import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.api.protein.repository.ProteinJpaRepository;
import com.yj.peuteu.api.user.application.FindUserService;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.util.LocalDateTimeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SaveProteinService {
    private final ProteinJpaRepository proteinJpaRepository;

    private final FindUserService findUserService;

    public Protein saveProtein(SaveProteinRequest request) {
        User user = findUserService.findUserEntity(request.getUserId());
        Protein protein = Protein.builder()
                .user(user)
                .food(request.getFood())
                .intake(request.getIntake())
                .intakeTime(LocalDateTimeConverter.toLocalDateTimeSecond(request.getIntakeTime()))
                .build();

        return proteinJpaRepository.save(protein);
    }
}
