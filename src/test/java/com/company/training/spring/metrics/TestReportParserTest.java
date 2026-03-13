package com.company.training.spring.metrics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestReportParserTest {

    private final TestReportParser parser = new TestReportParser();

    @TempDir
    Path tempDir;

    @Test
    void shouldAggregateSurefireAndJacocoReports() throws Exception {
        Path surefireDir = Files.createDirectory(tempDir.resolve("surefire-reports"));
        Files.write(surefireDir.resolve("TEST-suite-a.xml"), (
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<testsuite name=\"suite-a\" tests=\"5\" failures=\"1\" errors=\"0\" skipped=\"1\" time=\"1.5\"/>")
                .getBytes(StandardCharsets.UTF_8));
        Files.write(surefireDir.resolve("TEST-suite-b.xml"), (
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<testsuite name=\"suite-b\" tests=\"3\" failures=\"0\" errors=\"1\" skipped=\"0\" time=\"0.5\"/>")
                .getBytes(StandardCharsets.UTF_8));

        Path jacocoDir = Files.createDirectories(tempDir.resolve("site/jacoco"));
        Files.write(jacocoDir.resolve("jacoco.xml"), (
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                        + "<report name=\"demo\">"
                        + "<counter type=\"LINE\" missed=\"20\" covered=\"80\"/>"
                        + "<counter type=\"BRANCH\" missed=\"5\" covered=\"15\"/>"
                        + "</report>")
                .getBytes(StandardCharsets.UTF_8));

        TestMetricsSnapshot snapshot = parser.parse(surefireDir, jacocoDir.resolve("jacoco.xml"));

        assertEquals(8.0d, snapshot.getTotalTests());
        assertEquals(5.0d, snapshot.getPassedTests());
        assertEquals(1.0d, snapshot.getFailedTests());
        assertEquals(1.0d, snapshot.getErrorTests());
        assertEquals(1.0d, snapshot.getSkippedTests());
        assertEquals(2.0d, snapshot.getSuiteCount());
        assertEquals(2.0d, snapshot.getDurationSeconds());
        assertEquals(0.8d, snapshot.getCoverage().get(TestMetricsSnapshot.CoverageCounter.LINE).getRatio(), 0.0001d);
        assertEquals(0.75d, snapshot.getCoverage().get(TestMetricsSnapshot.CoverageCounter.BRANCH).getRatio(), 0.0001d);
    }

    @Test
    void shouldReturnEmptySnapshotWhenReportsAreMissing() {
        TestMetricsSnapshot snapshot = parser.parse(tempDir.resolve("missing-surefire"), tempDir.resolve("missing-jacoco.xml"));

        assertEquals(0.0d, snapshot.getTotalTests());
        assertEquals(0.0d, snapshot.getSuiteCount());
        assertEquals(0.0d, snapshot.getCoverage().get(TestMetricsSnapshot.CoverageCounter.LINE).getRatio(), 0.0001d);
    }
}
