package com.cloudsec.service;

import com.cloudsec.config.MetricsConfig;
import com.cloudsec.entity.DetectedRisk;
import com.cloudsec.entity.RemediationAction;
import com.cloudsec.repository.DetectedRiskRepository;
import com.cloudsec.repository.RemediationActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class RemediationService {

    private final RemediationActionRepository remediationActionRepository;
    private final DetectedRiskRepository detectedRiskRepository;
    private final MetricsConfig metricsConfig;

    public RemediationService(RemediationActionRepository remediationActionRepository,
            DetectedRiskRepository detectedRiskRepository,
            MetricsConfig metricsConfig) {
        this.remediationActionRepository = remediationActionRepository;
        this.detectedRiskRepository = detectedRiskRepository;
        this.metricsConfig = metricsConfig;
    }

    /**
     * Remediate a specific risk
     * Returns true if successful, false otherwise
     */
    @Transactional
    public boolean remediateRisk(Integer riskId) {
        try {
            log.info("Attempting to remediate risk: {}", riskId);

            DetectedRisk risk = detectedRiskRepository.findById(riskId)
                    .orElseThrow(() -> new IllegalArgumentException("Risk not found: " + riskId));

            boolean remediationSuccess = false;
            String actionType = "";

            // Route to appropriate remediation method
            switch (risk.getRiskType()) {
                case "PUBLIC_S3_BUCKET":
                    remediationSuccess = remediatePublicS3(risk);
                    actionType = "SET_S3_PRIVATE";
                    break;
                case "OPEN_SECURITY_GROUP":
                    remediationSuccess = remediateOpenSecurityGroup(risk);
                    actionType = "CLOSE_SECURITY_GROUP";
                    break;
                case "WILDCARD_PRINCIPAL":
                    remediationSuccess = remediateWildcardPrincipal(risk);
                    actionType = "RESTRICT_IAM_PRINCIPAL";
                    break;
                default:
                    log.warn("Unknown risk type: {}", risk.getRiskType());
                    remediationSuccess = false;
                    actionType = "UNKNOWN";
            }

            // Record the remediation action
            RemediationAction action = new RemediationAction();
            action.setRiskId(riskId);
            action.setActionType(actionType);
            action.setStatus(remediationSuccess ? "SUCCESS" : "FAILED");
            action.setTimestamp(LocalDateTime.now());
            remediationActionRepository.save(action);

            // Record metrics
            if (remediationSuccess) {
                metricsConfig.recordRemediationSuccess();
                log.info("Successfully remediated risk: {} with action: {}", riskId, actionType);
            } else {
                metricsConfig.recordRemediationFailure();
                log.warn("Failed to remediate risk: {} with action: {}", riskId, actionType);
            }

            return remediationSuccess;
        } catch (Exception e) {
            log.error("Error remediating risk: {}", riskId, e);
            return false;
        }
    }

    /**
     * Remediate public S3 bucket - set to private
     */
    private boolean remediatePublicS3(DetectedRisk risk) {
        try {
            // In production, this would call AWS SDK:
            // s3Client.putBucketAcl(PutBucketAclRequest.builder()
            // .bucket(resourceName)
            // .acl(ObjectCannedACL.PRIVATE)
            // .build());

            // For demo, simulate API call delay
            Thread.sleep(500);
            log.info("Mock remediation: Set S3 bucket to private (Resource ID: {})", risk.getResourceId());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while remediating S3 bucket", e);
            return false;
        } catch (Exception e) {
            log.error("Error remediating S3 bucket", e);
            return false;
        }
    }

    /**
     * Remediate open security group - restrict to specific IPs
     */
    private boolean remediateOpenSecurityGroup(DetectedRisk risk) {
        try {
            // In production, this would call AWS SDK:
            // ec2Client.revokeSecurityGroupIngress(RevokeSecurityGroupIngressRequest.builder()
            // .groupId(groupId)
            // .ipPermissions(ipPermission)
            // .build());

            // For demo, simulate API call delay
            Thread.sleep(500);
            log.info("Mock remediation: Removed 0.0.0.0/0 rule from security group (Resource ID: {})",
                    risk.getResourceId());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while remediating security group", e);
            return false;
        } catch (Exception e) {
            log.error("Error remediating security group", e);
            return false;
        }
    }

    /**
     * Remediate wildcard IAM principal
     */
    private boolean remediateWildcardPrincipal(DetectedRisk risk) {
        try {
            // In production, this would call AWS SDK:
            // iamClient.putRolePolicy(PutRolePolicyRequest.builder()
            // .roleName(roleName)
            // .policyDocument(restrictedPolicy)
            // .build());

            // For demo, simulate API call delay
            Thread.sleep(500);
            log.info("Mock remediation: Restricted IAM principal from * (Resource ID: {})", risk.getResourceId());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while remediating IAM policy", e);
            return false;
        } catch (Exception e) {
            log.error("Error remediating IAM policy", e);
            return false;
        }
    }

    /**
     * Get remediation status for a risk
     */
    public RemediationStatus getRemediationStatus(Integer riskId) {
        var actions = remediationActionRepository.findByRiskId(riskId);

        if (actions.isEmpty()) {
            return new RemediationStatus("NONE", null, 0);
        }

        RemediationAction latestAction = actions.get(actions.size() - 1);
        long attemptCount = actions.size();

        return new RemediationStatus(latestAction.getStatus(), latestAction.getTimestamp(), (int) attemptCount);
    }
}