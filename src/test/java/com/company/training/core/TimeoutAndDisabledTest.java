package com.company.training.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeout;

@DisplayName("超时与禁用测试示例")
class TimeoutAndDisabledTest {

    @Test
    void shouldCompleteWithin200ms() {
        assertTimeout(Duration.ofMillis(200), () -> {
            Thread.sleep(80);
            return "OK";
        });
    }

    @Test
    @Disabled("演示 @Disabled：依赖外部系统时可以临时关闭")
    void demoDisabledTest() {
        // no-op
    }
}
