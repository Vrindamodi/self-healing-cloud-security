import React from 'react';
import { DetectedRisk } from '../types';

interface RiskTableProps {
  risks: DetectedRisk[];
  loading: boolean;
  onRemediate: (riskId: number) => void;
  remediatingRisks: Set<number>;
  remediationStatus: { [key: number]: string };
}

const RiskTable: React.FC<RiskTableProps> = ({ 
  risks, 
  loading, 
  onRemediate, 
  remediatingRisks,
  remediationStatus 
}) => {
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

  const getStatusColor = (status: string | undefined): string => {
    switch (status) {
      case 'SUCCESS':
        return '#10b981';
      case 'FAILED':
        return '#ef4444';
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
        <p>No risks detected. Your infrastructure looks secure! 🎉</p>
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
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Status</th>
            <th style={{ padding: '12px', textAlign: 'left', fontWeight: '600' }}>Action</th>
          </tr>
        </thead>
        <tbody>
          {risks.map((risk, index) => {
            const isRemediating = remediatingRisks.has(risk.id);
            const status = remediationStatus[risk.id];

            return (
              <tr
                key={risk.id}
                style={{
                  borderBottom: '1px solid #e5e7eb',
                  backgroundColor: index % 2 === 0 ? '#ffffff' : '#f9fafb',
                  animation: status === 'SUCCESS' ? 'fadeIn 0.3s ease-in' : 'none',
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
                <td style={{ padding: '12px', maxWidth: '250px', wordBreak: 'break-word', fontSize: '13px' }}>
                  {risk.description}
                </td>
                <td style={{ padding: '12px' }}>
                  {status ? (
                    <span
                      style={{
                        backgroundColor: getStatusColor(status),
                        color: 'white',
                        padding: '4px 12px',
                        borderRadius: '4px',
                        fontWeight: '600',
                        fontSize: '12px',
                      }}
                    >
                      {status}
                    </span>
                  ) : (
                    <span style={{ color: '#6b7280', fontSize: '12px' }}>Pending</span>
                  )}
                </td>
                <td style={{ padding: '12px' }}>
                  <button
                    onClick={() => onRemediate(risk.id)}
                    disabled={isRemediating}
                    style={{
                      padding: '6px 12px',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: 'white',
                      backgroundColor: isRemediating ? '#9ca3af' : '#10b981',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: isRemediating ? 'not-allowed' : 'pointer',
                      transition: 'background-color 0.2s',
                    }}
                    onMouseEnter={(e) => {
                      if (!isRemediating) {
                        (e.target as HTMLButtonElement).style.backgroundColor = '#059669';
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (!isRemediating) {
                        (e.target as HTMLButtonElement).style.backgroundColor = '#10b981';
                      }
                    }}
                  >
                    {isRemediating ? '⏳ ...' : '🔧 Fix'}
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>

      <style>{`
        @keyframes fadeIn {
          from {
            opacity: 0.5;
          }
          to {
            opacity: 1;
          }
        }
      `}</style>
    </div>
  );
};

export default RiskTable;