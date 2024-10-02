package com.yj.peuteu.api.protein.repository;

import com.yj.peuteu.api.protein.domain.Protein;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProteinJpaRepository extends JpaRepository<Protein, Long> {
}