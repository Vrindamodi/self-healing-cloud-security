package com.cloudsec.api;

import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.service.CloudSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/risks")
public class RisksController {

    private final CloudSecurityService cloudSecurityService;

    public RisksController(CloudSecurityService cloudSecurityService) {
        this.cloudSecurityService = cloudSecurityService;
    }

    /**
     * Get all risks
     * GET /api/risks
     */
    @GetMapping
    public ResponseEntity<List<DetectedRisk>> getAllRisks() {
        try {
            List<DetectedRisk> risks = cloudSecurityService.getAllRisks();
            log.info("Retrieved {} risks", risks.size());
            return ResponseEntity.ok(risks);
        } catch (Exception e) {
            log.error("Error retrieving risks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get risks by severity
     * GET /api/risks?severity=HIGH
     */
    @GetMapping(params = "severity")
    public ResponseEntity<List<DetectedRisk>> getRisksBySeverity(@RequestParam String severity) {
        try {
            List<DetectedRisk> risks = cloudSecurityService.getRisksBySeverity(severity.toUpperCase());
            log.info("Retrieved {} risks with severity {}", risks.size(), severity);
            return ResponseEntity.ok(risks);
        } catch (Exception e) {
            log.error("Error retrieving risks by severity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get specific risk by ID
     * GET /api/risks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetectedRisk> getRiskById(@PathVariable Integer id) {
        try {
            DetectedRisk risk = cloudSecurityService.getRiskById(id);
            return ResponseEntity.ok(risk);
        } catch (IllegalArgumentException e) {
            log.warn("Risk not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error retrieving risk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}