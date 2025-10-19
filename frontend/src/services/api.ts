import axios from 'axios';
import { DetectedRisk, SecurityStats, ScanResponse } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

export const apiService = {
  /**
   * Trigger a security scan
   */
  performScan: async (): Promise<ScanResponse> => {
    const response = await apiClient.get<ScanResponse>('/scan');
    return response.data;
  },

  /**
   * Get all risks
   */
  getAllRisks: async (): Promise<DetectedRisk[]> => {
    const response = await apiClient.get<DetectedRisk[]>('/risks');
    return response.data;
  },

  /**
   * Get risks by severity
   */
  getRisksBySeverity: async (severity: string): Promise<DetectedRisk[]> => {
    const response = await apiClient.get<DetectedRisk[]>('/risks', {
      params: { severity },
    });
    return response.data;
  },

  /**
   * Get single risk
   */
  getRiskById: async (id: number): Promise<DetectedRisk> => {
    const response = await apiClient.get<DetectedRisk>(`/risks/${id}`);
    return response.data;
  },

  /**
   * Get security stats
   */
  getSecurityStats: async (): Promise<SecurityStats> => {
    const response = await apiClient.get<SecurityStats>('/stats');
    return response.data;
  },

  /**
   * Health check
   */
  healthCheck: async (): Promise<boolean> => {
    try {
      const response = await apiClient.get('/health');
      return response.status === 200;
    } catch {
      return false;
    }
  },

  /**
   * Manually trigger remediation
   */
  remediateRisk: async (riskId: number): Promise<any> => {
    const response = await apiClient.post(`/remediation/${riskId}`);
    return response.data;
  },

  /**
   * Get remediation status
   */
  getRemediationStatus: async (riskId: number): Promise<any> => {
    const response = await apiClient.get(`/remediation/${riskId}/status`);
    return response.data;
  },
};