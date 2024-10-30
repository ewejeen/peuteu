package com.yj.peuteu.api.protein.domain;

import com.yj.peuteu.api.user.domain.User;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "protein")
@Entity
public class Protein {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String food;

    private Double intake;

    private LocalDateTime intakeTime;
}
