package com.company.training.spring.metrics;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

@Component
public class TestReportParser {

    public TestMetricsSnapshot parse(TestMetricsProperties properties) {
        if (!properties.isEnabled()) {
            return TestMetricsSnapshot.empty();
        }

        Path basePath = Paths.get(System.getProperty("user.dir"));
        Path surefireDir = basePath.resolve(properties.getSurefireDir()).normalize();
        Path jacocoXml = basePath.resolve(properties.getJacocoXml()).normalize();

        SurefireSummary surefireSummary = parseSurefireReports(surefireDir);
        Map<TestMetricsSnapshot.CoverageCounter, TestMetricsSnapshot.CoverageSummary> coverage =
                parseJacocoCoverage(jacocoXml);

        return new TestMetricsSnapshot(
                surefireSummary.totalTests,
                surefireSummary.passedTests,
                surefireSummary.failedTests,
                surefireSummary.errorTests,
                surefireSummary.skippedTests,
                surefireSummary.suiteCount,
                surefireSummary.durationSeconds,
                surefireSummary.lastRunTimestampSeconds,
                coverage
        );
    }

    TestMetricsSnapshot parse(Path surefireDir, Path jacocoXml) {
        SurefireSummary surefireSummary = parseSurefireReports(surefireDir);
        Map<TestMetricsSnapshot.CoverageCounter, TestMetricsSnapshot.CoverageSummary> coverage =
                parseJacocoCoverage(jacocoXml);
        return new TestMetricsSnapshot(
                surefireSummary.totalTests,
                surefireSummary.passedTests,
                surefireSummary.failedTests,
                surefireSummary.errorTests,
                surefireSummary.skippedTests,
                surefireSummary.suiteCount,
                surefireSummary.durationSeconds,
                surefireSummary.lastRunTimestampSeconds,
                coverage
        );
    }

    private SurefireSummary parseSurefireReports(Path surefireDir) {
        SurefireSummary summary = new SurefireSummary();
        if (!Files.isDirectory(surefireDir)) {
            return summary;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(surefireDir, "TEST-*.xml")) {
            for (Path report : stream) {
                Document document = parseXml(report);
                Element root = document.getDocumentElement();
                summary.suiteCount += 1.0d;
                summary.totalTests += parseDouble(root.getAttribute("tests"));
                summary.failedTests += parseDouble(root.getAttribute("failures"));
                summary.errorTests += parseDouble(root.getAttribute("errors"));
                summary.skippedTests += parseDouble(root.getAttribute("skipped"));
                summary.durationSeconds += parseDouble(root.getAttribute("time"));
                summary.lastRunTimestampSeconds = Math.max(summary.lastRunTimestampSeconds, lastModifiedSeconds(report));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse surefire reports from " + surefireDir, ex);
        }

        summary.passedTests = Math.max(0.0d,
                summary.totalTests - summary.failedTests - summary.errorTests - summary.skippedTests);
        return summary;
    }

    private Map<TestMetricsSnapshot.CoverageCounter, TestMetricsSnapshot.CoverageSummary> parseJacocoCoverage(Path jacocoXml) {
        EnumMap<TestMetricsSnapshot.CoverageCounter, TestMetricsSnapshot.CoverageSummary> coverage =
                new EnumMap<TestMetricsSnapshot.CoverageCounter, TestMetricsSnapshot.CoverageSummary>(
                        TestMetricsSnapshot.CoverageCounter.class);
        for (TestMetricsSnapshot.CoverageCounter counter : TestMetricsSnapshot.CoverageCounter.values()) {
            coverage.put(counter, new TestMetricsSnapshot.CoverageSummary(0.0d, 0.0d));
        }

        if (!Files.exists(jacocoXml)) {
            return coverage;
        }

        try {
            Document document = parseXml(jacocoXml);
            Element root = document.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (!(child instanceof Element)) {
                    continue;
                }
                Element element = (Element) child;
                if (!"counter".equals(element.getTagName())) {
                    continue;
                }
                TestMetricsSnapshot.CoverageCounter counter = TestMetricsSnapshot.CoverageCounter
                        .valueOf(element.getAttribute("type"));
                coverage.put(counter, new TestMetricsSnapshot.CoverageSummary(
                        parseDouble(element.getAttribute("covered")),
                        parseDouble(element.getAttribute("missed"))));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse JaCoCo report from " + jacocoXml, ex);
        }

        return coverage;
    }

    private Document parseXml(Path path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        String content = new String(Files.readAllBytes(path), "UTF-8");
        String sanitized = content.replaceFirst("<!DOCTYPE[^>]*>", "");
        return builder.parse(new InputSource(new StringReader(sanitized)));
    }

    private double lastModifiedSeconds(Path path) throws IOException {
        return Files.getLastModifiedTime(path).toMillis() / 1000.0d;
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0d;
        }
        return Double.parseDouble(value);
    }

    private static final class SurefireSummary {
        private double totalTests;
        private double passedTests;
        private double failedTests;
        private double errorTests;
        private double skippedTests;
        private double suiteCount;
        private double durationSeconds;
        private double lastRunTimestampSeconds;
    }
}
