package com.cloudsec.service;

import com.cloudsec.config.MetricsConfig;
import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.entity.RemediationAction;
import com.cloudsec.repository.DetectedRiskRepository;
import com.cloudsec.repository.RemediationActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Remediation Service Unit Tests")
public class RemediationServiceTest {

    @Mock
    private DetectedRiskRepository detectedRiskRepository;

    @Mock
    private RemediationActionRepository remediationActionRepository;

    @Mock
    private MetricsConfig metricsConfig;

    private RemediationService remediationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        remediationService = new RemediationService(
                remediationActionRepository,
                detectedRiskRepository,
                metricsConfig);
    }

    @Test
    @DisplayName("Should successfully remediate S3 risk")
    public void testRemediateS3RiskSuccess() {
        // Arrange
        DetectedRisk risk = new DetectedRisk();
        risk.setId(1);
        risk.setResourceId(100);
        risk.setRiskType("PUBLIC_S3_BUCKET");
        risk.setSeverity("HIGH");
        risk.setDescription("Public S3 bucket");

        when(detectedRiskRepository.findById(1)).thenReturn(Optional.of(risk));
        when(remediationActionRepository.save(any(RemediationAction.class)))
                .thenReturn(new RemediationAction());

        // Act
        boolean result = remediationService.remediateRisk(1);

        // Assert
        assertTrue(result);
        verify(remediationActionRepository, times(1)).save(any(RemediationAction.class));
        verify(metricsConfig, times(1)).recordRemediationSuccess();
    }

    @Test
    @DisplayName("Should handle non-existent risk gracefully")
    public void testRemediateNonExistentRisk() {
        // Arrange
        when(detectedRiskRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        boolean result = remediationService.remediateRisk(999);

        // Assert
        assertFalse(result);
        verify(metricsConfig, never()).recordRemediationSuccess();
    }

    @Test
    @DisplayName("Should record remediation status")
    public void testGetRemediationStatus() {
        // Arrange
        RemediationAction action = new RemediationAction();
        action.setId(1);
        action.setRiskId(1);
        action.setStatus("SUCCESS");
        action.setTimestamp(LocalDateTime.now());

        when(remediationActionRepository.findByRiskId(1))
                .thenReturn(java.util.List.of(action));

        // Act
        RemediationStatus status = remediationService.getRemediationStatus(1);

        // Assert
        assertNotNull(status);
        assertEquals("SUCCESS", status.getStatus());
        assertEquals(1, status.getAttemptCount());
    }

    @Test
    @DisplayName("Should return NONE status for risk with no remediation")
    public void testGetRemediationStatusNone() {
        // Arrange
        when(remediationActionRepository.findByRiskId(1))
                .thenReturn(java.util.List.of());

        // Act
        RemediationStatus status = remediationService.getRemediationStatus(1);

        // Assert
        assertNotNull(status);
        assertEquals("NONE", status.getStatus());
    }
}