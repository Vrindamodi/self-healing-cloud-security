package com.cloudsec.api;

import com.cloudsec.service.RemediationService;
import com.cloudsec.service.RemediationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/remediation")
public class RemediationController {

    private final RemediationService remediationService;

    public RemediationController(RemediationService remediationService) {
        this.remediationService = remediationService;
    }

    /**
     * Manually trigger remediation for a specific risk
     * POST /api/remediation/{riskId}
     */
    @PostMapping("/{riskId}")
    public ResponseEntity<RemediationResponse> remediateRisk(@PathVariable Integer riskId) {
        try {
            log.info("Remediation endpoint called for risk: {}", riskId);
            boolean success = remediationService.remediateRisk(riskId);

            if (success) {
                return ResponseEntity
                        .ok(new RemediationResponse("success", riskId, "Remediation completed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new RemediationResponse("failed", riskId, "Remediation failed"));
            }
        } catch (Exception e) {
            log.error("Error remediating risk: {}", riskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RemediationResponse("error", riskId, e.getMessage()));
        }
    }

    /**
     * Get remediation status for a risk
     * GET /api/remediation/{riskId}/status
     */
    @GetMapping("/{riskId}/status")
    public ResponseEntity<RemediationStatus> getRemediationStatus(@PathVariable Integer riskId) {
        try {
            RemediationStatus status = remediationService.getRemediationStatus(riskId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting remediation status for risk: {}", riskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static class RemediationResponse {
        public String status;
        public Integer riskId;
        public String message;

        public RemediationResponse(String status, Integer riskId, String message) {
            this.status = status;
            this.riskId = riskId;
            this.message = message;
        }
    }
}