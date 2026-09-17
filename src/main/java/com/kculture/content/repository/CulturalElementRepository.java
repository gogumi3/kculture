package com.kculture.content.repository;

import com.kculture.content.domain.CulturalElement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CulturalElementRepository extends JpaRepository<CulturalElement, Long> {
}
