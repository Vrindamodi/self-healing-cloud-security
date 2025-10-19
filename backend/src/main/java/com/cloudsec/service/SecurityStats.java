package com.cloudsec.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityStats {
    private int totalRisks;
    private int highSeverity;
    private int mediumSeverity;
    private int lowSeverity;
}
