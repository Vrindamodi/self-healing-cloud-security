export interface DetectedRisk {
  id: number;
  resourceId: number;
  riskType: string;
  severity: 'HIGH' | 'MEDIUM' | 'LOW';
  description: string;
  detectedAt: string;
}

export interface SecurityStats {
  totalRisks: number;
  highSeverity: number;
  mediumSeverity: number;
  lowSeverity: number;
}

export interface ScanResponse {
  status: 'success' | 'error';
  risksDetected: number;
  risks: DetectedRisk[];
}