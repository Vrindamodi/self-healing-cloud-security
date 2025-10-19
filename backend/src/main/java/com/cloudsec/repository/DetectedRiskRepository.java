package com.cloudsec.repository;

import com.cloudsec.entity.DetectedRisk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetectedRiskRepository extends JpaRepository<DetectedRisk, Integer> {
    List<DetectedRisk> findBySeverity(String severity);

    Page<DetectedRisk> findBySeverity(String severity, Pageable pageable);

    List<DetectedRisk> findByResourceId(Integer resourceId);
}
