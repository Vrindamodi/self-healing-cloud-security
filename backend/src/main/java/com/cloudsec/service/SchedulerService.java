package com.cloudsec.service;

import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.repository.DetectedRiskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SchedulerService {

    private final CloudSecurityService cloudSecurityService;
    private final RemediationService remediationService;
    private final DetectedRiskRepository detectedRiskRepository;

    @Value("${scheduler.detection.enabled:true}")
    private boolean detectionEnabled;

    @Value("${scheduler.remediation.enabled:true}")
    private boolean remediationEnabled;

    @Value("${scheduler.remediation.auto-remediate-high:true}")
    private boolean autoRemediateHigh;

    public SchedulerService(CloudSecurityService cloudSecurityService,
            RemediationService remediationService,
            DetectedRiskRepository detectedRiskRepository) {
        this.cloudSecurityService = cloudSecurityService;
        this.remediationService = remediationService;
        this.detectedRiskRepository = detectedRiskRepository;
    }

    /**
     * Run security scan every 5 minutes
     * Configurable via: scheduler.detection.interval (in milliseconds)
     */
    @Scheduled(fixedRateString = "${scheduler.detection.interval:300000}")
    public void runScheduledDetection() {
        if (!detectionEnabled) {
            return;
        }

        try {
            log.info("===== SCHEDULED DETECTION STARTED =====");
            long startTime = System.currentTimeMillis();

            List<DetectedRisk> risks = cloudSecurityService.performScan();

            long duration = System.currentTimeMillis() - startTime;
            log.info("===== SCHEDULED DETECTION COMPLETED =====");
            log.info("Detected {} risks in {}ms", risks.size(), duration);

            // Record metric
            if (!risks.isEmpty()) {
                log.info("Risk breakdown - HIGH: {}, MEDIUM: {}, LOW: {}",
                        risks.stream().filter(r -> "HIGH".equals(r.getSeverity())).count(),
                        risks.stream().filter(r -> "MEDIUM".equals(r.getSeverity())).count(),
                        risks.stream().filter(r -> "LOW".equals(r.getSeverity())).count());
            }
        } catch (Exception e) {
            log.error("Error in scheduled detection", e);
        }
    }

    /**
     * Auto-remediate HIGH severity risks every 2 minutes
     * Configurable via: scheduler.remediation.interval (in milliseconds)
     */
    @Scheduled(fixedRateString = "${scheduler.remediation.interval:120000}")
    public void runScheduledRemediation() {
        if (!remediationEnabled || !autoRemediateHigh) {
            return;
        }

        try {
            log.info("===== SCHEDULED REMEDIATION STARTED =====");

            // Get all HIGH severity risks
            List<DetectedRisk> highRisks = detectedRiskRepository.findBySeverity("HIGH");

            if (highRisks.isEmpty()) {
                log.info("No HIGH severity risks to remediate");
                return;
            }

            log.info("Found {} HIGH severity risks. Attempting auto-remediation...", highRisks.size());

            int successCount = 0;
            int failureCount = 0;

            for (DetectedRisk risk : highRisks) {
                boolean success = remediationService.remediateRisk(risk.getId());
                if (success) {
                    successCount++;
                } else {
                    failureCount++;
                }
            }

            log.info("===== SCHEDULED REMEDIATION COMPLETED =====");
            log.info("Remediation results - Success: {}, Failures: {}", successCount, failureCount);

        } catch (Exception e) {
            log.error("Error in scheduled remediation", e);
        }
    }

    /**
     * Health check - runs every 1 minute
     * Verifies database connectivity and service health
     */
    @Scheduled(fixedRate = 60000)
    public void runHealthCheck() {
        try {
            long count = detectedRiskRepository.count();
            log.debug("Health check: Database is healthy. Total risks in DB: {}", count);
        } catch (Exception e) {
            log.error("Health check failed - Database connectivity issue", e);
        }
    }
}