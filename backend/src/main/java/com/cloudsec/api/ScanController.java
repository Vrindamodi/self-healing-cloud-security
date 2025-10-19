package com.cloudsec.api;

import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.service.CloudSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/scan")
public class ScanController {

    private final CloudSecurityService cloudSecurityService;

    public ScanController(CloudSecurityService cloudSecurityService) {
        this.cloudSecurityService = cloudSecurityService;
    }

    /**
     * Trigger a security scan
     * POST /api/scan
     */
    @GetMapping
    public ResponseEntity<ScanResponse> performScan() {
        try {
            log.info("Scan endpoint called");
            List<DetectedRisk> risks = cloudSecurityService.performScan();
            return ResponseEntity.ok(new ScanResponse("success", risks.size(), risks));
        } catch (Exception e) {
            log.error("Error during scan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ScanResponse("error", 0, null));
        }
    }

    public static class ScanResponse {
        public String status;
        public int risksDetected;
        public List<DetectedRisk> risks;

        public ScanResponse(String status, int risksDetected, List<DetectedRisk> risks) {
            this.status = status;
            this.risksDetected = risksDetected;
            this.risks = risks;
        }
    }
}