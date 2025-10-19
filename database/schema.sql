-- Cloud Security Database Schema

-- Cloud Resources Table
CREATE TABLE IF NOT EXISTS cloud_resources (
    id SERIAL PRIMARY KEY,
    resource_type VARCHAR(50) NOT NULL,
    resource_name VARCHAR(255) NOT NULL,
    location VARCHAR(100),
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Detected Risks Table
CREATE TABLE IF NOT EXISTS detected_risks (
    id SERIAL PRIMARY KEY,
    resource_id INTEGER NOT NULL,
    risk_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description TEXT,
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES cloud_resources(id) ON DELETE CASCADE
);

-- Remediation Actions Table
CREATE TABLE IF NOT EXISTS remediation_actions (
    id SERIAL PRIMARY KEY,
    risk_id INTEGER NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (risk_id) REFERENCES detected_risks(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_resources_type ON cloud_resources(resource_type);
CREATE INDEX idx_risks_severity ON detected_risks(severity);
CREATE INDEX idx_risks_resource_id ON detected_risks(resource_id);
CREATE INDEX idx_remediation_risk_id ON remediation_actions(risk_id);
CREATE INDEX idx_remediation_status ON remediation_actions(status);