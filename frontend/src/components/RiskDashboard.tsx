import React, { useState, useEffect } from 'react';
import { DetectedRisk, SecurityStats } from '../types';
import { apiService } from '../services/api';
import ScanButton from './ScanButton';
import StatsCard from './StatsCard';
import RiskTable from './RiskTable';

const RiskDashboard: React.FC = () => {
  const [risks, setRisks] = useState<DetectedRisk[]>([]);
  const [stats, setStats] = useState<SecurityStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [scanLoading, setScanLoading] = useState(false);
  const [apiHealth, setApiHealth] = useState(false);
  const [remediationStatus, setRemediationStatus] = useState<{ [key: number]: string }>({});
  const [remediatingRisks, setRemediatingRisks] = useState<Set<number>>(new Set());
  const [lastScanTime, setLastScanTime] = useState<string>('Never');

  // Check API health on mount
  useEffect(() => {
    const checkHealth = async () => {
      const healthy = await apiService.healthCheck();
      setApiHealth(healthy);
    };
    checkHealth();
  }, []);

  // Fetch risks and stats
  const fetchData = async () => {
    try {
      setLoading(true);
      const [risksData, statsData] = await Promise.all([
        apiService.getAllRisks(),
        apiService.getSecurityStats(),
      ]);
      setRisks(risksData);
      setStats(statsData);
      setLastScanTime(new Date().toLocaleTimeString());
    } catch (error) {
      console.error('Error fetching data:', error);
      setApiHealth(false);
    } finally {
      setLoading(false);
    }
  };

  // Handle scan button click
  const handleScan = async () => {
    try {
      setScanLoading(true);
      await apiService.performScan();
      // Fetch updated data after scan
      await fetchData();
    } catch (error) {
      console.error('Error performing scan:', error);
      alert('Failed to perform scan. Make sure the backend is running.');
    } finally {
      setScanLoading(false);
    }
  };

  // Handle remediation
  const handleRemediate = async (riskId: number) => {
    try {
      setRemediatingRisks((prev) => new Set([...prev, riskId]));
      const response = await apiService.remediateRisk(riskId);
      
      if (response.status === 'success') {
        setRemediationStatus((prev) => ({
          ...prev,
          [riskId]: 'SUCCESS',
        }));
      } else {
        setRemediationStatus((prev) => ({
          ...prev,
          [riskId]: 'FAILED',
        }));
      }

      // Refresh data after remediation
      await fetchData();
    } catch (error) {
      console.error('Error remediating risk:', error);
      setRemediationStatus((prev) => ({
        ...prev,
        [riskId]: 'FAILED',
      }));
    } finally {
      setRemediatingRisks((prev) => {
        const newSet = new Set(prev);
        newSet.delete(riskId);
        return newSet;
      });
    }
  };

  // Fetch data on mount
  useEffect(() => {
    fetchData();
  }, []);

  // Poll for updates every 10 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      fetchData();
    }, 10000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: '#f9fafb',
        padding: '24px',
        fontFamily:
          '-apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen", "Ubuntu", "Cantarell", sans-serif',
      }}
    >
      {/* Header */}
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ margin: '0 0 8px 0', fontSize: '32px', fontWeight: 'bold' }}>
          🔐 Cloud Security Dashboard
        </h1>
        <p style={{ margin: '0', color: '#6b7280', fontSize: '16px' }}>
          Real-time security risk detection and remediation
        </p>
        <p style={{ margin: '8px 0 0 0', color: '#6b7280', fontSize: '12px' }}>
          Last updated: {lastScanTime}
        </p>
        {!apiHealth && (
          <p style={{ margin: '8px 0 0 0', color: '#ef4444', fontSize: '14px' }}>
            ⚠️ Backend API is not responding. Make sure it's running on http://localhost:8080
          </p>
        )}
        {apiHealth && (
          <p style={{ margin: '8px 0 0 0', color: '#10b981', fontSize: '14px' }}>
            ✅ Backend connected and healthy
          </p>
        )}
      </div>

      {/* Stats Cards */}
      <StatsCard stats={stats} loading={loading} />

      {/* Controls */}
      <div style={{ marginBottom: '24px', display: 'flex', gap: '12px', alignItems: 'center', flexWrap: 'wrap' }}>
        <ScanButton onScan={handleScan} loading={scanLoading} />
        <button
          onClick={fetchData}
          disabled={loading}
          style={{
            padding: '10px 24px',
            fontSize: '16px',
            fontWeight: '600',
            color: '#3b82f6',
            backgroundColor: 'white',
            border: '2px solid #3b82f6',
            borderRadius: '6px',
            cursor: loading ? 'not-allowed' : 'pointer',
            transition: 'all 0.2s',
            opacity: loading ? 0.6 : 1,
          }}
          onMouseEnter={(e) => {
            if (!loading) {
              (e.target as HTMLButtonElement).style.backgroundColor = '#eff6ff';
            }
          }}
          onMouseLeave={(e) => {
            if (!loading) {
              (e.target as HTMLButtonElement).style.backgroundColor = 'white';
            }
          }}
        >
          🔄 Refresh
        </button>
      </div>

      {/* Risks Table */}
      <div
        style={{
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
          padding: '24px',
        }}
      >
        <h2 style={{ margin: '0 0 16px 0', fontSize: '20px', fontWeight: '600' }}>
          Detected Risks ({risks.length})
        </h2>
        <RiskTable 
          risks={risks} 
          loading={loading}
          onRemediate={handleRemediate}
          remediatingRisks={remediatingRisks}
          remediationStatus={remediationStatus}
        />
      </div>
    </div>
  );
};

export default RiskDashboard;