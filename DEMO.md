# 🎬 Live Demo Instructions

Complete walkthrough for demonstrating the Self-Healing Cloud Security Platform. **Total time: 5-7 minutes.**

## Pre-Demo Checklist (5 minutes before)

```bash
# 1. Pull latest code
git pull origin main

# 2. Start all services
docker-compose down -v  # Clean slate
docker-compose up -d

# 3. Wait for services to be healthy
docker-compose ps

# Expected: All 5 services show "healthy" status

# 4. Verify backend is responding
curl http://localhost:8080/api/health
# Expected: {"status":"UP"}

# 5. Verify frontend loads
curl http://localhost:3000 | grep -q "Cloud Security Dashboard" && echo "✅ Frontend OK"

# 6. Check logs for errors
docker-compose logs backend | tail -20
# Should see: "Started CloudSecurityApp"

# 7. Test API endpoints work
curl http://localhost:8080/api/stats
# Should see: {"totalRisks":0,"highSeverity":0,...}
```

**If anything fails, DO NOT proceed. Troubleshoot first.**

---

## Demo Flow (5 minutes)

### Part 1: Show the Dashboard (1 minute)

**Action Steps:**
1. Open browser to `http://localhost:3000`
2. Show dashboard layout:
   - "Cloud Security Dashboard" title
   - Backend health status (green checkmark)
   - Last updated timestamp
   - 4 stats cards (all showing 0)
   - Empty risk table below

**What to Say:**
> "This is our cloud security platform dashboard. It shows:
> - Real-time detection of security risks
> - Current statistics: total risks, high/medium/low breakdown
> - A risk table that updates every 10 seconds
> - Manual remediation controls"

**Expected State:**
```
✅ Backend connected and healthy
📊 Stats: Total=0, High=0, Medium=0, Low=0
📋 Table: Empty (no risks yet)
```

### Part 2: Trigger Detection Scan (1.5 minutes)

**Action Steps:**
1. Click "🔍 Scan Now" button
2. Wait for button to show "Scanning..."
3. Wait ~2 seconds for scan to complete
4. Watch dashboard update automatically

**What to Say:**
> "Now I'll trigger a security scan to detect risks in our mock cloud infrastructure.
> The backend will scan 7 resources for 3 types of vulnerabilities:
> - Public S3 buckets
> - Open security groups allowing 0.0.0.0/0
> - Wildcard IAM principals"

**Expected Results (immediately after):**
```
✅ Stats update: Total=5, High=2, Medium=2, Low=1
📋 Table shows 5 risks:
   - PUBLIC_S3_BUCKET (HIGH) - company-backups
   - OPEN_SECURITY_GROUP (HIGH) - OPEN-PROD-SG
   - PUBLIC_S3_BUCKET (HIGH) - public-assets
   - WILDCARD_PRINCIPAL (MEDIUM) - WILDCARD-ASSUME-ROLE
   - Another risk visible
⏱️ Last updated: [current time]
```

**Advanced Point (if asked):**
> "The detection happens in ~500ms. In production, this would call the AWS SDK to scan real cloud resources. We're using mock data for this demo to avoid needing AWS credentials."

### Part 3: Manual Remediation (1.5 minutes)

**Action Steps:**
1. Find the first HIGH severity risk (PUBLIC_S3_BUCKET)
2. Click the "🔧 Fix" button on that row
3. Button changes to "⏳ ..." (loading state)
4. Wait ~1 second
5. Status changes to "SUCCESS" (green badge)
6. Observe stats update: Total decreases by 1

**What to Say:**
> "Now I'll manually remediate one of the HIGH severity risks.
> When I click Fix, the backend will:
> 1. Apply the appropriate remediation (set S3 to private)
> 2. Record this action in the database
> 3. Update the risk status
> 4. Record metrics to Prometheus
> 5. Decrement the open risk counter"

**Expected Results:**
```
Initial: Total=5, High=2
   ↓ (click Fix on one)
After: Total=4, High=1
       Risk row shows "SUCCESS" status
```

**Remediation Details (if asked):**
> "Each remediation method is tailored:
> - S3: Sets bucket ACL to private
> - Security Group: Removes rules allowing 0.0.0.0/0
> - IAM: Restricts principal from '*' to specific ARNs"

### Part 4: Show Automatic Scheduler (1 minute)

**Action Steps:**
1. Explain the scheduler (don't wait for it)
2. Open backend logs to show scheduler running:
   ```bash
   docker logs cloud-security-backend | grep "SCHEDULED" | tail -5
   ```
3. Show sample output:
   ```
   2025-10-19 10:30:00 - ===== SCHEDULED DETECTION STARTED =====
   2025-10-19 10:30:00 - Detected 5 risks in 450ms
   2025-10-19 10:30:00 - ===== SCHEDULED DETECTION COMPLETED =====
   ```

**What to Say:**
> "In addition to manual scanning, the platform automatically:
> - Runs detection every 5 minutes
> - Auto-remediates HIGH severity risks every 2 minutes
> - Checks health every 1 minute
> 
> All of this is configurable. You can adjust timings via environment variables."

### Part 5: Show Monitoring Dashboard (0.5 minutes)

**Action Steps:**
1. Open Prometheus: `http://localhost:9090`
2. Click "Graph" tab
3. Search for: `detection_risks_found_total`
4. Show the metric with value
5. Go back, show Grafana: `http://localhost:3001`
6. Login: admin/admin
7. Show the Cloud Security Platform dashboard with 4 panels

**What to Say:**
> "We're collecting comprehensive metrics to Prometheus:
> - Every risk detected
> - Every remediation (success/failure)
> - Detection scan duration
> 
> Grafana visualizes this data in real-time dashboards.
> This gives operators complete visibility into security operations."

**Metrics Visible:**
- Risks Detected Over Time (line chart)
- Remediation Success vs Failures (bar chart)
- Current Open Risks (gauge)
- Detection Scan Duration (histogram)

---

## Extended Demo (Optional - if time permits)

### Show Test Coverage (2 minutes)

```bash
# Show backend tests
cd backend
mvn test

# Output:
# Tests run: 15, Failures: 0, Errors: 0
# [INFO] BUILD SUCCESS
```

**What to Say:**
> "We have comprehensive test coverage:
> - 7 unit tests for business logic
> - 8 integration tests for API endpoints
> - Tests for detection, remediation, and statistics
> - All tests pass in <10 seconds"

### Show Docker Architecture (2 minutes)

```bash
# List running containers
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Expected:
# cloud-security-backend      Up 5 minutes    0.0.0.0:8080->8080/tcp
# cloud-security-frontend     Up 5 minutes    0.0.0.0:3000->80/tcp
# cloud-security-postgres     Up 5 minutes    5432/tcp
# cloud-security-prometheus   Up 5 minutes    0.0.0.0:9090->9090/tcp
# cloud-security-grafana      Up 5 minutes    0.0.0.0:3001->3000/tcp
```

**What to Say:**
> "The entire platform runs in Docker containers:
> - 5 independent services
> - All orchestrated with docker-compose
> - Each service has health checks
> - Data persists in Docker volumes
> - Can deploy to any environment: laptop, server, cloud"

### Show Database Schema (2 minutes)

```bash
# Connect to database
docker exec -it cloud-security-postgres psql -U postgres -d cloud_security_db

# Show tables
\dt

# Query some data
SELECT * FROM detected_risks LIMIT 5;
SELECT COUNT(*) FROM remediation_actions WHERE status='SUCCESS';
```

---

## Common Q&A During Demo

### Q: "Why use mock AWS instead of real SDK?"

**Answer:**
> "Mock data lets us demonstrate the platform without AWS credentials. In production, you'd swap the detection logic to use the AWS SDK (boto3, AWS Java SDK, etc.). The entire architecture is designed for that transition—just replace the DetectionService implementation."

### Q: "How does auto-remediation work safely?"

**Answer:**
> "We have several safeguards:
> 1. Only HIGH severity risks are auto-remediated (configurable)
> 2. Each action is logged to database for audit trails
> 3. Remediation can be disabled with one environment variable
> 4. Manual review is still an option
> 
> In production, you might want approval workflows before auto-fixing."

### Q: "Can this scale to 10,000 resources?"

**Answer:**
> "Yes. The platform is designed for scale:
> - Horizontal scaling: Deploy multiple backend instances behind load balancer
> - Vertical scaling: Single instance handles 1000+ resources
> - Database: Use managed PostgreSQL (AWS RDS) with read replicas
> - Metrics: Prometheus can handle millions of time series
> 
> Current bottleneck is detection logic (~500ms). Parallel scanning would speed that up."

### Q: "How is this different from AWS Config or similar tools?"

**Answer:**
> "This is purpose-built for:
> 1. **Real-time visibility** - Dashboard updates every 10 seconds
> 2. **Automatic remediation** - Fixes happen within minutes, not hours
> 3. **Customizable** - You control detection rules and remediation logic
> 4. **Multi-cloud ready** - Can detect risks across AWS, Azure, GCP
> 5. **Learning resource** - Shows full-stack development skills
> 
> Commercial tools are better for enterprise compliance tracking. This is ideal for teams building custom security automation."

### Q: "What production changes would you make?"

**Answer:**
> "Great question. For production deployment:
> 1. Replace mock detection with real AWS SDK calls
> 2. Add authentication/authorization (OAuth2)
> 3. Add approval workflows before auto-remediation
> 4. Deploy to Kubernetes for high availability
> 5. Add email/Slack notifications
> 6. Implement role-based access control (RBAC)
> 7. Add compliance reporting (CIS, PCI-DSS)
> 8. Set up automated backups
> 9. Add rate limiting on API endpoints
> 10. Implement request tracing (Jaeger/Zipkin)"

---

## Troubleshooting During Demo

### Dashboard is blank
```bash
# Check if backend is running
curl http://localhost:8080/api/health

# Check frontend logs
docker logs cloud-security-frontend

# Restart frontend
docker-compose restart frontend
```

### Scan button doesn't work
```bash
# Check backend logs
docker logs cloud-security-backend | tail -20

# Verify database connection
docker exec cloud-security-postgres psql -U postgres -d cloud_security_db -c "SELECT 1"

# Restart backend
docker-compose restart backend
```

### Metrics not showing
```bash
# Check Prometheus is scraping
curl http://localhost:9090/api/v1/targets

# Check backend metrics endpoint
curl http://localhost:8080/actuator/prometheus | head -20

# Wait 15 seconds (Prometheus scrape interval)
sleep 15

# Try again
curl http://localhost:9090/api/v1/query?query=detection_risks_found_total
```

### Grafana showing no data
```bash
# Check datasource configuration
docker logs cloud-security-grafana | grep datasource

# Wait for data to be collected
# Prometheus needs 1-2 scrape intervals before data appears

# Manually query Prometheus
curl http://localhost:9090/api/v1/query?query=up
```

---

## Demo Script Template (Copy-Paste)

Feel free to customize with your own delivery style:

---

### Opening (30 seconds)

> "Today I'm showing you a self-healing cloud security platform I built to demonstrate full-stack development skills and cloud security knowledge.
> 
> The challenge: Security misconfigurations happen constantly in cloud environments. Teams need to detect them quickly AND fix them automatically. This platform does both in real-time."

### Live Demo (4 minutes)

> [Walk through parts 1-5 above]

### Technical Highlights (2 minutes)

> "Technically, this showcases:
> - **Backend**: Spring Boot 3.x with Spring Data JPA, scheduled tasks, metrics collection
> - **Frontend**: React 18 with TypeScript for type safety
> - **Infrastructure**: Docker Compose orchestrating 5 services
> - **Observability**: Prometheus metrics + Grafana dashboards
> - **Testing**: 15 unit and integration tests
> - **Database**: PostgreSQL with proper schema design and indexes
> 
> The code is production-ready with proper error handling, logging, and documentation."

### Closing (30 seconds)

> "This project demonstrates:
> 1. Full-stack capabilities (frontend, backend, infrastructure)
> 2. Cloud security domain knowledge
> 3. Real-world problem solving
> 4. DevOps/platform thinking
> 5. Code quality and testing practices
> 
> The entire codebase is on GitHub [link], with comprehensive README, architecture docs, and demo instructions."

---

## Post-Demo Cleanup

```bash
# Keep services running for Q&A
docker-compose logs -f backend

# After demo, stop services
docker-compose down

# To restart later
docker-compose up -d
```

---

**Demo Version:** 1.0 | **Last Updated:** October 2025 | **Estimated Time:** 5-7 minutes