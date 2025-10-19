package com.cloudsec.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemediationStatus {
    private String status; // SUCCESS, FAILED, NONE
    private LocalDateTime lastAttempt;
    private int attemptCount;
}