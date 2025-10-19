import React from 'react';
import { DetectedRisk } from '../types';

interface RiskTableProps {
  risks: DetectedRisk[];
  loading: boolean;
}

const RiskTable: React.FC<RiskTableProps> = ({ risks, loading }) => {
  const getSeverityColor = (severity: string): string => {
    switch (severity) {
      case 'HIGH':
        return '#ef4444';
      case 'MEDIUM':
        return '#f97316';
      case 'LOW':
        return '#eab308';
      default:
        return '#6b7280';
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '20px', textAlign: 'center' }}>
        <p>Loading risks...</p>
      </div>
    );
  }

  if (risks.length === 0) {
    return (
      <div style={{ padding: '20px', textAlign: 'center', color: '#6b7280' }}>
        <p>No risks detected. Your infrastructure looks secure!</p>
      </div>
    );
  }

  return (
    <div style={{ overflowX: 'auto', marginTop: '20px' }}>
      <table
        style={{
          width: '100%',
          borderCollapse: 'collapse',
          fontSize: '14px',
        }}
      >
        <thead>
          <tr style={{ backgroundColor: '#f3f4f6', borderBottom: '2px solid #e5e7eb' }}>
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Risk Type</th>
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Severity</th>
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Resource ID</th>
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Description</th>
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Detected At</th>
          </tr>
        </thead>
        <tbody>
          {risks.map((risk, index) => (
            <tr
              key={risk.id}
              style={{
                borderBottom: '1px solid #e5e7eb',
                backgroundColor: index % 2 === 0 ? '#ffffff' : '#f9fafb',
              }}
            >
              <td style={{ padding: '12px' }}>
                <code style={{ backgroundColor: '#f3f4f6', padding: '4px 8px', borderRadius: '4px' }}>
                  {risk.riskType}
                </code>
              </td>
              <td style={{ padding: '12px' }}>
                <span
                  style={{
                    backgroundColor: getSeverityColor(risk.severity),
                    color: 'white',
                    padding: '4px 12px',
                    borderRadius: '4px',
                    fontWeight: '600',
                    fontSize: '12px',
                  }}
                >
                  {risk.severity}
                </span>
              </td>
              <td style={{ padding: '12px' }}>{risk.resourceId}</td>
              <td style={{ padding: '12px', maxWidth: '300px', wordBreak: 'break-word' }}>
                {risk.description}
              </td>
              <td style={{ padding: '12px', fontSize: '12px', color: '#6b7280' }}>
                {new Date(risk.detectedAt).toLocaleString()}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default RiskTable;