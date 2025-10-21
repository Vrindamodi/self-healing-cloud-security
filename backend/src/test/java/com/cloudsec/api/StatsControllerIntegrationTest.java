package com.cloudsec.api;

import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.repository.DetectedRiskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Stats Controller Integration Tests")
public class StatsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DetectedRiskRepository detectedRiskRepository;

    @BeforeEach
    public void setUp() {
        detectedRiskRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/stats should return security statistics")
    public void testGetSecurityStats() throws Exception {
        // Arrange
        DetectedRisk highRisk = new DetectedRisk();
        highRisk.setResourceId(1);
        highRisk.setRiskType("PUBLIC_S3_BUCKET");
        highRisk.setSeverity("HIGH");
        highRisk.setDescription("Test HIGH risk");
        highRisk.setDetectedAt(LocalDateTime.now());
        detectedRiskRepository.save(highRisk);

        DetectedRisk mediumRisk = new DetectedRisk();
        mediumRisk.setResourceId(2);
        mediumRisk.setRiskType("OPEN_SECURITY_GROUP");
        mediumRisk.setSeverity("MEDIUM");
        mediumRisk.setDescription("Test MEDIUM risk");
        mediumRisk.setDetectedAt(LocalDateTime.now());
        detectedRiskRepository.save(mediumRisk);

        // Act & Assert
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRisks").value(2))
                .andExpect(jsonPath("$.highSeverity").value(1))
                .andExpect(jsonPath("$.mediumSeverity").value(1))
                .andExpect(jsonPath("$.lowSeverity").value(0));
    }

    @Test
    @DisplayName("GET /api/stats should return zero when no risks")
    public void testGetStatsEmpty() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRisks").value(0));
    }
}