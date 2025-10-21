package com.cloudsec.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Scan Controller Integration Tests")
public class ScanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/scan should return success with detected risks")
    public void testPerformScan() throws Exception {
        mockMvc.perform(get("/api/scan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.risksDetected").isNumber())
                .andExpect(jsonPath("$.risks").isArray());
    }

    @Test
    @DisplayName("POST /api/scan should detect multiple risks")
    public void testScanDetectsMultipleRisks() throws Exception {
        mockMvc.perform(get("/api/scan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.risksDetected").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("POST /api/scan risks should have required fields")
    public void testScanRisksHaveRequiredFields() throws Exception {
        mockMvc.perform(get("/api/scan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.risks[0].id").isNumber())
                .andExpect(jsonPath("$.risks[0].riskType").isString())
                .andExpect(jsonPath("$.risks[0].severity").isString())
                .andExpect(jsonPath("$.risks[0].description").isString());
    }
}