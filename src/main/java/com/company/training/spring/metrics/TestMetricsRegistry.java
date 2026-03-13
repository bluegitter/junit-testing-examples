package com.company.training.spring.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
@ConditionalOnProperty(prefix = "training.metrics.test", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TestMetricsRegistry implements MeterBinder {

    private static final Logger log = LoggerFactory.getLogger(TestMetricsRegistry.class);

    private final TestReportParser parser;
    private final TestMetricsProperties properties;
    private final AtomicReference<TestMetricsSnapshot> snapshot =
            new AtomicReference<TestMetricsSnapshot>(TestMetricsSnapshot.empty());

    public TestMetricsRegistry(TestReportParser parser, TestMetricsProperties properties) {
        this.parser = parser;
        this.properties = properties;
        refresh();
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("training_unit_test_suites_total", snapshot, this::suiteCount)
                .description("Number of JUnit test suites discovered from surefire reports")
                .register(registry);
        Gauge.builder("training_unit_test_duration_seconds", snapshot, this::durationSeconds)
                .description("Total duration of the latest JUnit test run")
                .register(registry);
        Gauge.builder("training_unit_test_last_run_timestamp_seconds", snapshot, this::lastRunTimestampSeconds)
                .description("Last modified timestamp of the latest surefire report")
                .register(registry);
        Gauge.builder("training_unit_test_success_rate", snapshot, this::successRate)
                .description("Passed test cases divided by total test cases")
                .register(registry);

        registerStatusGauge(registry, "total");
        registerStatusGauge(registry, "passed");
        registerStatusGauge(registry, "failed");
        registerStatusGauge(registry, "error");
        registerStatusGauge(registry, "skipped");

        for (TestMetricsSnapshot.CoverageCounter counter : TestMetricsSnapshot.CoverageCounter.values()) {
            Gauge.builder("training_unit_test_coverage_ratio", snapshot,
                            current -> current.get().getCoverage().get(counter).getRatio())
                    .description("JaCoCo coverage ratio for a given counter")
                    .tag("counter", counter.name().toLowerCase())
                    .register(registry);
            Gauge.builder("training_unit_test_coverage_covered_total", snapshot,
                            current -> current.get().getCoverage().get(counter).getCovered())
                    .description("Covered JaCoCo items for a given counter")
                    .tag("counter", counter.name().toLowerCase())
                    .register(registry);
            Gauge.builder("training_unit_test_coverage_missed_total", snapshot,
                            current -> current.get().getCoverage().get(counter).getMissed())
                    .description("Missed JaCoCo items for a given counter")
                    .tag("counter", counter.name().toLowerCase())
                    .register(registry);
        }
    }

    @Scheduled(fixedDelayString = "${training.metrics.test.refresh-interval:30000}")
    public void refresh() {
        try {
            snapshot.set(parser.parse(properties));
        } catch (RuntimeException ex) {
            log.warn("Failed to refresh unit test metrics from reports, keeping previous snapshot", ex);
        }
    }

    public TestMetricsSnapshot getSnapshot() {
        return snapshot.get();
    }

    private void registerStatusGauge(MeterRegistry registry, String status) {
        Gauge.builder("training_unit_test_cases_total", snapshot, current -> statusValue(current, status))
                .description("JUnit test case totals grouped by status")
                .tag("status", status)
                .register(registry);
    }

    private double statusValue(AtomicReference<TestMetricsSnapshot> current, String status) {
        TestMetricsSnapshot snapshot = current.get();
        if ("total".equals(status)) {
            return snapshot.getTotalTests();
        }
        if ("passed".equals(status)) {
            return snapshot.getPassedTests();
        }
        if ("failed".equals(status)) {
            return snapshot.getFailedTests();
        }
        if ("error".equals(status)) {
            return snapshot.getErrorTests();
        }
        if ("skipped".equals(status)) {
            return snapshot.getSkippedTests();
        }
        return 0.0d;
    }

    private double suiteCount(AtomicReference<TestMetricsSnapshot> current) {
        return current.get().getSuiteCount();
    }

    private double durationSeconds(AtomicReference<TestMetricsSnapshot> current) {
        return current.get().getDurationSeconds();
    }

    private double lastRunTimestampSeconds(AtomicReference<TestMetricsSnapshot> current) {
        return current.get().getLastRunTimestampSeconds();
    }

    private double successRate(AtomicReference<TestMetricsSnapshot> current) {
        return current.get().getSuccessRate();
    }
}
