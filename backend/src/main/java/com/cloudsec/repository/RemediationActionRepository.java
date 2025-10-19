package com.cloudsec.repository;

import com.cloudsec.entity.RemediationAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemediationActionRepository extends JpaRepository<RemediationAction, Integer> {
    List<RemediationAction> findByRiskId(Integer riskId);

    List<RemediationAction> findByStatus(String status);
}