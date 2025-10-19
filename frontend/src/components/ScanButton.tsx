import React from 'react';

interface ScanButtonProps {
  onScan: () => void;
  loading: boolean;
}

const ScanButton: React.FC<ScanButtonProps> = ({ onScan, loading }) => {
  return (
    <button
      onClick={onScan}
      disabled={loading}
      style={{
        padding: '10px 24px',
        fontSize: '16px',
        fontWeight: '600',
        color: 'white',
        backgroundColor: loading ? '#9ca3af' : '#3b82f6',
        border: 'none',
        borderRadius: '6px',
        cursor: loading ? 'not-allowed' : 'pointer',
        transition: 'background-color 0.2s',
      }}
      onMouseEnter={(e) => {
        if (!loading) {
          (e.target as HTMLButtonElement).style.backgroundColor = '#2563eb';
        }
      }}
      onMouseLeave={(e) => {
        if (!loading) {
          (e.target as HTMLButtonElement).style.backgroundColor = '#3b82f6';
        }
      }}
    >
      {loading ? 'Scanning...' : '🔍 Scan Now'}
    </button>
  );
};

export default ScanButton;