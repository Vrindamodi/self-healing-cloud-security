// package com.cloudsec.service;

// import com.cloudsec.entity.DetectedRisk;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;

// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// @DisplayName("Detection Service Unit Tests")
// public class DetectionServiceTest {

// private DetectionService detectionService;

// @BeforeEach
// public void setUp() {
// detectionService = new DetectionService();
// }

// @Test
// @DisplayName("Should detect risks and return non-empty list")
// public void testDetectRisksReturnsNonEmpty() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// assertNotNull(risks);
// assertFalse(risks.isEmpty());
// assertTrue(risks.size() > 0);
// }

// @Test
// @DisplayName("Should detect at least one HIGH severity risk")
// public void testDetectHighSeverityRisks() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// long highSeverityCount = risks.stream()
// .filter(r -> "HIGH".equals(r.getSeverity()))
// .count();

// assertTrue(highSeverityCount > 0, "Should detect at least one HIGH severity
// risk");
// }

// @Test
// @DisplayName("Should detect PUBLIC_S3_BUCKET risk")
// public void testDetectPublicS3Bucket() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// boolean hasS3Risk = risks.stream()
// .anyMatch(r -> "PUBLIC_S3_BUCKET".equals(r.getRiskType()));

// assertTrue(hasS3Risk, "Should detect at least one PUBLIC_S3_BUCKET risk");
// }

// @Test
// @DisplayName("Should detect OPEN_SECURITY_GROUP risk")
// public void testDetectOpenSecurityGroup() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// boolean hasSecurityGroupRisk = risks.stream()
// .anyMatch(r -> "OPEN_SECURITY_GROUP".equals(r.getRiskType()));

// assertTrue(hasSecurityGroupRisk, "Should detect at least one
// OPEN_SECURITY_GROUP risk");
// }

// @Test
// @DisplayName("Should detect WILDCARD_PRINCIPAL risk")
// public void testDetectWildcardPrincipal() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// boolean hasWildcardRisk = risks.stream()
// .anyMatch(r -> "WILDCARD_PRINCIPAL".equals(r.getRiskType()));

// assertTrue(hasWildcardRisk, "Should detect at least one WILDCARD_PRINCIPAL
// risk");
// }

// @Test
// @DisplayName("Should have descriptions for all risks")
// public void testRisksHaveDescriptions() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// for (DetectedRisk risk : risks) {
// assertNotNull(risk.getDescription());
// assertFalse(risk.getDescription().isEmpty());
// assertTrue(risk.getDescription().length() > 10);
// }
// }

// @Test
// @DisplayName("Should have detection timestamp for all risks")
// public void testRisksHaveTimestamp() {
// List<DetectedRisk> risks = detectionService.detectRisks();

// for (DetectedRisk risk : risks) {
// assertNotNull(risk.getDetectedAt());
// }
// }
// }
