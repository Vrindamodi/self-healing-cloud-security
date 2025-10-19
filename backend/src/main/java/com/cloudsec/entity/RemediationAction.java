package com.cloudsec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "remediation_actions", indexes = {
        @Index(name = "idx_remediation_risk_id", columnList = "risk_id"),
        @Index(name = "idx_remediation_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemediationAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer riskId;

    @Column(nullable = false, length = 100)
    private String actionType;

    @Column(nullable = false, length = 20)
    private String status; // SUCCESS, FAILED, PENDING

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}