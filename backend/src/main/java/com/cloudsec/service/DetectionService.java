package com.cloudsec.service;

import com.cloudsec.entity.CloudResource;
import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.repository.CloudResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DetectionService {

    private final CloudResourceRepository cloudResourceRepository;

    public DetectionService(CloudResourceRepository cloudResourceRepository) {
        this.cloudResourceRepository = cloudResourceRepository;
    }

    /**
     * Detect security risks in cloud resources
     * Uses mock data for MVP - in production, would call AWS SDK
     */
    public List<DetectedRisk> detectRisks() {
        log.info("Starting security scan...");
        List<DetectedRisk> detectedRisks = new ArrayList<>();

        // Generate mock cloud resources and scan them
        List<CloudResource> resources = generateMockResources();

        // Save resources to database so they get IDs
        List<CloudResource> savedResources = cloudResourceRepository.saveAll(resources);

        for (CloudResource resource : savedResources) {
            detectedRisks.addAll(scanResource(resource));
        }

        log.info("Scan complete. Found {} risks", detectedRisks.size());
        return detectedRisks;
    }

    /**
     * Scan individual resource for risks
     */
    private List<DetectedRisk> scanResource(CloudResource resource) {
        List<DetectedRisk> risks = new ArrayList<>();

        switch (resource.getResourceType()) {
            case "S3_BUCKET":
                risks.addAll(checkS3Risks(resource));
                break;
            case "SECURITY_GROUP":
                risks.addAll(checkSecurityGroupRisks(resource));
                break;
            case "IAM_POLICY":
                risks.addAll(checkIAMRisks(resource));
                break;
            default:
                log.warn("Unknown resource type: {}", resource.getResourceType());
        }

        return risks;
    }

    /**
     * Check for S3 bucket risks
     */
    private List<DetectedRisk> checkS3Risks(CloudResource resource) {
        List<DetectedRisk> risks = new ArrayList<>();

        // Mock check: 50% of S3 buckets are public
        if (resource.getIsPublic()) {
            DetectedRisk risk = new DetectedRisk();
            risk.setResourceId(resource.getId());
            risk.setRiskType("PUBLIC_S3_BUCKET");
            risk.setSeverity("HIGH");
            risk.setDescription("S3 bucket " + resource.getResourceName()
                    + " is publicly accessible. Anyone on the internet can read/write objects.");
            risk.setDetectedAt(LocalDateTime.now());
            risks.add(risk);
            log.warn("Found public S3 bucket: {}", resource.getResourceName());
        }

        return risks;
    }

    /**
     * Check for Security Group risks
     */
    private List<DetectedRisk> checkSecurityGroupRisks(CloudResource resource) {
        List<DetectedRisk> risks = new ArrayList<>();

        // Mock check: some security groups have open 0.0.0.0/0 rules
        if (resource.getResourceName().contains("OPEN")) {
            DetectedRisk risk = new DetectedRisk();
            risk.setResourceId(resource.getId());
            risk.setRiskType("OPEN_SECURITY_GROUP");
            risk.setSeverity("HIGH");
            risk.setDescription("Security group " + resource.getResourceName()
                    + " allows traffic from 0.0.0.0/0. This exposes your resources to the entire internet.");
            risk.setDetectedAt(LocalDateTime.now());
            risks.add(risk);
            log.warn("Found open security group: {}", resource.getResourceName());
        }

        return risks;
    }

    /**
     * Check for IAM Policy risks
     */
    private List<DetectedRisk> checkIAMRisks(CloudResource resource) {
        List<DetectedRisk> risks = new ArrayList<>();

        // Mock check: some IAM policies have Principal: "*"
        if (resource.getResourceName().contains("WILDCARD")) {
            DetectedRisk risk = new DetectedRisk();
            risk.setResourceId(resource.getId());
            risk.setRiskType("WILDCARD_PRINCIPAL");
            risk.setSeverity("MEDIUM");
            risk.setDescription("IAM policy " + resource.getResourceName()
                    + " has Principal: '*'. This allows any AWS principal to assume this role.");
            risk.setDetectedAt(LocalDateTime.now());
            risks.add(risk);
            log.warn("Found wildcard IAM principal: {}", resource.getResourceName());
        }

        return risks;
    }

    /**
     * Generate mock cloud resources for demo
     * In production, these would come from AWS
     */
    private List<CloudResource> generateMockResources() {
        List<CloudResource> resources = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // S3 Buckets
        resources.add(new CloudResource(null, "S3_BUCKET", "company-backups", "us-east-1", true, now, now));
        resources.add(new CloudResource(null, "S3_BUCKET", "internal-data-bucket", "eu-west-1", false, now, now));
        resources.add(new CloudResource(null, "S3_BUCKET", "public-assets", "us-west-2", true, now, now));

        // Security Groups
        resources.add(new CloudResource(null, "SECURITY_GROUP", "OPEN-PROD-SG", "us-east-1", false, now, now));
        resources.add(new CloudResource(null, "SECURITY_GROUP", "locked-down-sg", "eu-west-1", false, now, now));

        // IAM Policies
        resources.add(new CloudResource(null, "IAM_POLICY", "WILDCARD-ASSUME-ROLE", "us-east-1", false, now, now));
        resources.add(new CloudResource(null, "IAM_POLICY", "limited-policy", "us-east-1", false, now, now));

        return resources;
    }
}