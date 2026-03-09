package com.company.training.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("高级断言与 Assumption 示例")
class AssertionsAndAssumptionsTest {

    private final Calculator calculator = new Calculator();
    private final StringNormalizer normalizer = new StringNormalizer();
    private final DiscountCalculator discountCalculator = new DiscountCalculator();

    @Test
    void shouldValidateMultipleResultsUsingAssertAll() {
        assertAll("核心结果检查",
                () -> assertEquals(9, calculator.add(4, 5)),
                () -> assertEquals("Alice", normalizer.normalizeName("aLiCe")),
                () -> assertEquals(80.0, discountCalculator.calculateFinalPrice(100, "VIP"), 0.0001)
        );
    }

    @Test
    void shouldSkipWhenPreConditionNotMatched() {
        String runIntegrationLikeCase = System.getProperty("run.integration.like.case", "false");
        assumeTrue("true".equalsIgnoreCase(runIntegrationLikeCase),
                "仅用于演示 assumeTrue：默认会被跳过");
        assertEquals(1, 1);
    }

    @Test
    void shouldNotThrowForValidInput() {
        assertDoesNotThrow(() -> discountCalculator.calculateFinalPrice(88.0, "NORMAL"));
    }
}
