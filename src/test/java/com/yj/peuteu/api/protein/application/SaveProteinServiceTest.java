package com.yj.peuteu.api.protein.application;

import com.yj.peuteu.api.protein.domain.Protein;
import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.api.protein.repository.ProteinJpaRepository;
import com.yj.peuteu.common.util.LocalDateTimeConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SaveProteinServiceTest {

    @Autowired
    private ProteinJpaRepository proteinJpaRepository;


    // https://velog.io/@chrkb1569/Junit-Service-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1
    @Test
    @DisplayName("프로틴 저장 테스트")
    void saveProtein() {

        // given
        SaveProteinRequest proteinRequest = new SaveProteinRequest("1", "음식", 15.5, "2024-10-02 17:41:22");

        Protein protein = Protein.builder()
                .user(null)
                .food(proteinRequest.getFood())
                .intake(proteinRequest.getIntake())
                .intakeTime(LocalDateTimeConverter.toLocalDateTimeSecond(proteinRequest.getIntakeTime()))
                .build();

        Protein p = proteinJpaRepository.save(protein);

        // when


        // then
        Protein savedP = proteinJpaRepository.findById(p.getId()).orElse(null);
        Assertions.assertEquals(p.getId(), savedP.getId());
    }
}