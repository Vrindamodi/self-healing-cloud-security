package com.cloudsec.api;

import com.cloudsec.service.CloudSecurityService;
import com.cloudsec.service.SecurityStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final CloudSecurityService cloudSecurityService;

    public StatsController(CloudSecurityService cloudSecurityService) {
        this.cloudSecurityService = cloudSecurityService;
    }

    /**
     * Get security statistics
     * GET /api/stats
     */
    @GetMapping
    public ResponseEntity<SecurityStats> getSecurityStats() {
        try {
            SecurityStats stats = cloudSecurityService.getSecurityStats();
            log.info("Retrieved security stats: total={}, high={}", stats.getTotalRisks(), stats.getHighSeverity());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}