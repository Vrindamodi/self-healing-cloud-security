package com.cloudsec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cloud_resources", indexes = {
        @Index(name = "idx_resources_type", columnList = "resource_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String resourceType;

    @Column(nullable = false, length = 255)
    private String resourceName;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
