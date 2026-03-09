package com.company.training.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("重复测试与动态测试示例")
class RepeatedAndDynamicTest {

    private final StringNormalizer normalizer = new StringNormalizer();

    @RepeatedTest(value = 3, name = "第 {currentRepetition} / {totalRepetitions} 次")
    void shouldRunRepeatedly(RepetitionInfo repetitionInfo) {
        assertTrue(repetitionInfo.getCurrentRepetition() >= 1);
        assertTrue(repetitionInfo.getCurrentRepetition() <= repetitionInfo.getTotalRepetitions());
    }

    @TestFactory
    @DisplayName("动态生成多个姓名标准化测试")
    List<DynamicTest> shouldGenerateDynamicTests() {
        List<String> rawNames = Arrays.asList(" TOM", "jErrY", "  spike ");
        List<String> expectedNames = Arrays.asList("Tom", "Jerry", "Spike");

        return Arrays.asList(0, 1, 2).stream()
                .map(i -> DynamicTest.dynamicTest(
                        "动态用例-" + i,
                        () -> assertEquals(expectedNames.get(i), normalizer.normalizeName(rawNames.get(i)))
                ))
                .collect(Collectors.toList());
    }
}
