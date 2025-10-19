package com.cloudsec;

import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.repository.DetectedRiskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class RisksControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DetectedRiskRepository detectedRiskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        detectedRiskRepository.deleteAll();
    }

    @Test
    public void testGetAllRisks() throws Exception {
        // Arrange
        DetectedRisk risk = new DetectedRisk();
        risk.setResourceId(1);
        risk.setRiskType("PUBLIC_S3_BUCKET");
        risk.setSeverity("HIGH");
        risk.setDescription("Test risk");
        risk.setDetectedAt(LocalDateTime.now());
        detectedRiskRepository.save(risk);

        // Act & Assert
        mockMvc.perform(get("/api/risks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].riskType").value("PUBLIC_S3_BUCKET"))
                .andExpect(jsonPath("$[0].severity").value("HIGH"));
    }

    @Test
    public void testGetRisksBySeverity() throws Exception {
        // Arrange
        DetectedRisk highRisk = new DetectedRisk();
        highRisk.setResourceId(1);
        highRisk.setRiskType("PUBLIC_S3_BUCKET");
        highRisk.setSeverity("HIGH");
        highRisk.setDescription("High severity risk");
        highRisk.setDetectedAt(LocalDateTime.now());
        detectedRiskRepository.save(highRisk);

        DetectedRisk lowRisk = new DetectedRisk();
        lowRisk.setResourceId(2);
        lowRisk.setRiskType("WEAK_POLICY");
        lowRisk.setSeverity("LOW");
        lowRisk.setDescription("Low severity risk");
        lowRisk.setDetectedAt(LocalDateTime.now());
        detectedRiskRepository.save(lowRisk);

        // Act & Assert
        mockMvc.perform(get("/api/risks?severity=HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].severity").value("HIGH"));
    }

    @Test
    public void testGetRiskById() throws Exception {
        // Arrange
        DetectedRisk risk = new DetectedRisk();
        risk.setResourceId(1);
        risk.setRiskType("PUBLIC_S3_BUCKET");
        risk.setSeverity("HIGH");
        risk.setDescription("Test risk");
        risk.setDetectedAt(LocalDateTime.now());
        DetectedRisk savedRisk = detectedRiskRepository.save(risk);

        // Act & Assert
        mockMvc.perform(get("/api/risks/" + savedRisk.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskType").value("PUBLIC_S3_BUCKET"));
    }

    @Test
    public void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}