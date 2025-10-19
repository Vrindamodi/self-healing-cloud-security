package com.cloudsec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "detected_risks", indexes = {
        @Index(name = "idx_risks_severity", columnList = "severity"),
        @Index(name = "idx_risks_resource_id", columnList = "resource_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectedRisk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer resourceId;

    @Column(nullable = false, length = 100)
    private String riskType;

    @Column(nullable = false, length = 20)
    private String severity; // HIGH, MEDIUM, LOW

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();
}
