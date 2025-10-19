import React from 'react';
import { SecurityStats } from '../types';

interface StatsCardProps {
  stats: SecurityStats | null;
  loading: boolean;
}

const StatsCard: React.FC<StatsCardProps> = ({ stats, loading }) => {
  const StatItem = ({
    label,
    value,
    color,
  }: {
    label: string;
    value: number;
    color: string;
  }) => (
    <div
      style={{
        flex: 1,
        padding: '20px',
        borderRadius: '8px',
        backgroundColor: color,
        color: 'white',
        textAlign: 'center',
      }}
    >
      <p style={{ margin: '0 0 8px 0', fontSize: '14px', opacity: 0.9 }}>{label}</p>
      <p style={{ margin: '0', fontSize: '32px', fontWeight: 'bold' }}>{value}</p>
    </div>
  );

  return (
    <div
      style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
        gap: '16px',
        marginBottom: '24px',
      }}
    >
      {loading ? (
        <p>Loading stats...</p>
      ) : stats ? (
        <>
          <StatItem label="Total Risks" value={stats.totalRisks} color="#3b82f6" />
          <StatItem label="High Severity" value={stats.highSeverity} color="#ef4444" />
          <StatItem label="Medium Severity" value={stats.mediumSeverity} color="#f97316" />
          <StatItem label="Low Severity" value={stats.lowSeverity} color="#eab308" />
        </>
      ) : (
        <p>No stats available</p>
      )}
    </div>
  );
};

export default StatsCard;