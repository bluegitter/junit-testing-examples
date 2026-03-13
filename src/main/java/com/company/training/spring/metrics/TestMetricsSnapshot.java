package com.company.training.spring.metrics;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class TestMetricsSnapshot {

    public enum CoverageCounter {
        INSTRUCTION,
        BRANCH,
        LINE,
        COMPLEXITY,
        METHOD,
        CLASS
    }

    public static final class CoverageSummary {
        private final double covered;
        private final double missed;

        public CoverageSummary(double covered, double missed) {
            this.covered = covered;
            this.missed = missed;
        }

        public double getCovered() {
            return covered;
        }

        public double getMissed() {
            return missed;
        }

        public double getRatio() {
            double total = covered + missed;
            return total == 0 ? 0.0d : covered / total;
        }
    }

    private final double totalTests;
    private final double passedTests;
    private final double failedTests;
    private final double errorTests;
    private final double skippedTests;
    private final double suiteCount;
    private final double durationSeconds;
    private final double lastRunTimestampSeconds;
    private final Map<CoverageCounter, CoverageSummary> coverage;

    public TestMetricsSnapshot(double totalTests,
                               double passedTests,
                               double failedTests,
                               double errorTests,
                               double skippedTests,
                               double suiteCount,
                               double durationSeconds,
                               double lastRunTimestampSeconds,
                               Map<CoverageCounter, CoverageSummary> coverage) {
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.errorTests = errorTests;
        this.skippedTests = skippedTests;
        this.suiteCount = suiteCount;
        this.durationSeconds = durationSeconds;
        this.lastRunTimestampSeconds = lastRunTimestampSeconds;
        this.coverage = Collections.unmodifiableMap(new EnumMap<CoverageCounter, CoverageSummary>(coverage));
    }

    public static TestMetricsSnapshot empty() {
        EnumMap<CoverageCounter, CoverageSummary> emptyCoverage =
                new EnumMap<CoverageCounter, CoverageSummary>(CoverageCounter.class);
        for (CoverageCounter counter : CoverageCounter.values()) {
            emptyCoverage.put(counter, new CoverageSummary(0.0d, 0.0d));
        }
        return new TestMetricsSnapshot(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, emptyCoverage);
    }

    public double getTotalTests() {
        return totalTests;
    }

    public double getPassedTests() {
        return passedTests;
    }

    public double getFailedTests() {
        return failedTests;
    }

    public double getErrorTests() {
        return errorTests;
    }

    public double getSkippedTests() {
        return skippedTests;
    }

    public double getSuiteCount() {
        return suiteCount;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public double getLastRunTimestampSeconds() {
        return lastRunTimestampSeconds;
    }

    public double getSuccessRate() {
        return totalTests == 0.0d ? 0.0d : passedTests / totalTests;
    }

    public Map<CoverageCounter, CoverageSummary> getCoverage() {
        return coverage;
    }
}
