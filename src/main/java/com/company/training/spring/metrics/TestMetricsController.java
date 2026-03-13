package com.company.training.spring.metrics;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-metrics")
@ConditionalOnBean(TestMetricsRegistry.class)
public class TestMetricsController {

    private final TestMetricsRegistry registry;

    public TestMetricsController(TestMetricsRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/summary")
    public TestMetricsSnapshot summary() {
        return registry.getSnapshot();
    }

    @PostMapping("/refresh")
    public TestMetricsSnapshot refresh() {
        registry.refresh();
        return registry.getSnapshot();
    }
}
