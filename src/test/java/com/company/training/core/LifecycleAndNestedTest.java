package com.company.training.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("生命周期和嵌套测试示例")
class LifecycleAndNestedTest {

    private List<String> logs;

    @BeforeEach
    void setUp() {
        logs = new ArrayList<String>();
        logs.add("init");
    }

    @AfterEach
    void tearDown() {
        logs.clear();
    }

    @Test
    void shouldHaveInitLog() {
        assertEquals(1, logs.size());
        assertEquals("init", logs.get(0));
    }

    @Nested
    @DisplayName("当执行业务步骤时")
    class BusinessStep {

        @Test
        void shouldAppendStepLog() {
            logs.add("step-1");
            assertEquals(2, logs.size());
            assertEquals("step-1", logs.get(1));
        }
    }
}
