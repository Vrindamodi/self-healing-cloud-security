package com.cloudsec.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsConfig {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger currentOpenRisks = new AtomicInteger(0);

    public Counter risksDetectedCounter;
    public Counter remediationSuccessCounter;
    public Counter remediationFailureCounter;
    public Timer detectionDurationTimer;

    public MetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initializeMetrics();
    }

    private void initializeMetrics() {
        // Counters
        this.risksDetectedCounter = Counter.builder("detection_risks_found_total")
                .description("Total number of risks detected")
                .register(meterRegistry);

        this.remediationSuccessCounter = Counter.builder("remediation_actions_success_total")
                .description("Total number of successful remediations")
                .register(meterRegistry);

        this.remediationFailureCounter = Counter.builder("remediation_actions_failed_total")
                .description("Total number of failed remediations")
                .register(meterRegistry);

        // Gauge
        Gauge.builder("current_open_risks", currentOpenRisks, AtomicInteger::get)
                .description("Current number of open/unresolved risks")
                .register(meterRegistry);

        // Timer
        this.detectionDurationTimer = Timer.builder("detection_duration_seconds")
                .description("Time taken to complete detection scan")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    public void recordDetectionDuration(long milliseconds) {
        detectionDurationTimer.record(java.time.Duration.ofMillis(milliseconds));
    }

    public void recordRiskDetected() {
        risksDetectedCounter.increment();
        currentOpenRisks.incrementAndGet();
    }

    public void recordRemediationSuccess() {
        remediationSuccessCounter.increment();
        currentOpenRisks.decrementAndGet();
    }

    public void recordRemediationFailure() {
        remediationFailureCounter.increment();
    }

    public void setCurrentOpenRisks(int count) {
        currentOpenRisks.set(count);
    }
}