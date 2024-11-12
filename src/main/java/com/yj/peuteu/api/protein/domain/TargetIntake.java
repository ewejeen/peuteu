package com.yj.peuteu.api.protein.domain;

import com.yj.peuteu.api.user.domain.User;
import com.yj.peuteu.common.domain.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "target_intake")
@Entity
public class TargetIntake extends BaseCreatedEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private Double target;
}
