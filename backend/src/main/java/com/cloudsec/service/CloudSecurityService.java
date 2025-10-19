package com.cloudsec.service;

import com.cloudsec.entity.CloudResource;
import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.repository.CloudResourceRepository;
import com.cloudsec.repository.DetectedRiskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CloudSecurityService {

    private final DetectionService detectionService;
    private final CloudResourceRepository cloudResourceRepository;
    private final DetectedRiskRepository detectedRiskRepository;

    public CloudSecurityService(DetectionService detectionService,
            CloudResourceRepository cloudResourceRepository,
            DetectedRiskRepository detectedRiskRepository) {
        this.detectionService = detectionService;
        this.cloudResourceRepository = cloudResourceRepository;
        this.detectedRiskRepository = detectedRiskRepository;
    }

    /**
     * Run a complete security scan
     * 1. Detect risks
     * 2. Save to database
     * 3. Return detected risks
     */
    @Transactional
    public List<DetectedRisk> performScan() {
        log.info("Performing security scan...");

        long startTime = System.currentTimeMillis();
        List<DetectedRisk> detectedRisks = detectionService.detectRisks();
        long duration = System.currentTimeMillis() - startTime;

        // Save all detected risks to database
        for (DetectedRisk risk : detectedRisks) {
            detectedRiskRepository.save(risk);
        }

        log.info("Scan completed in {}ms. Detected {} risks", duration, detectedRisks.size());
        return detectedRisks;
    }

    /**
     * Get all detected risks with optional filtering
     */
    public List<DetectedRisk> getAllRisks() {
        return detectedRiskRepository.findAll();
    }

    /**
     * Get risks by severity level
     */
    public List<DetectedRisk> getRisksBySeverity(String severity) {
        return detectedRiskRepository.findBySeverity(severity);
    }

    /**
     * Get paginated risks by severity
     */
    public Page<DetectedRisk> getRisksBySeverityPaginated(String severity, Pageable pageable) {
        return detectedRiskRepository.findBySeverity(severity, pageable);
    }

    /**
     * Get specific risk by ID
     */
    public DetectedRisk getRiskById(Integer riskId) {
        return detectedRiskRepository.findById(riskId)
                .orElseThrow(() -> new IllegalArgumentException("Risk not found: " + riskId));
    }

    /**
     * Get security statistics
     */
    public SecurityStats getSecurityStats() {
        List<DetectedRisk> allRisks = detectedRiskRepository.findAll();

        long highSeverity = allRisks.stream()
                .filter(r -> "HIGH".equals(r.getSeverity()))
                .count();

        long mediumSeverity = allRisks.stream()
                .filter(r -> "MEDIUM".equals(r.getSeverity()))
                .count();

        long lowSeverity = allRisks.stream()
                .filter(r -> "LOW".equals(r.getSeverity()))
                .count();

        return new SecurityStats(
                allRisks.size(),
                (int) highSeverity,
                (int) mediumSeverity,
                (int) lowSeverity);
    }
}