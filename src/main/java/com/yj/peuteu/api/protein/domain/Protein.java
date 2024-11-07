package com.yj.peuteu.api.protein.domain;

import com.yj.peuteu.api.protein.dto.request.SaveProteinRequest;
import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.domain.BaseEntity;
import com.yj.peuteu.common.enums.DeleteYn;
import com.yj.peuteu.common.util.LocalDateTimeConverter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "protein")
@Entity
public class Protein extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String food;

    private Double intake;

    private LocalDateTime intakeTime;

    private DeleteYn deleteYn;

    public Protein update(SaveProteinRequest request) {
        this.food = request.getFood();
        this.intake = request.getIntake();
        this.intakeTime = LocalDateTimeConverter.toLocalDateTimeSecond(request.getIntakeTime());
        return this;
    }

    public void delete() {
        this.deleteYn = DeleteYn.Y;
    }
}
