package com.company.training.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("MethodSource 参数化测试示例")
class DiscountCalculatorMethodSourceTest {

    private final DiscountCalculator discountCalculator = new DiscountCalculator();

    @ParameterizedTest(name = "原价{0}，等级{1}，最终价应为{2}")
    @MethodSource("priceCases")
    void shouldCalculateFinalPriceByLevel(double amount, String level, double expected) {
        assertEquals(expected, discountCalculator.calculateFinalPrice(amount, level), 0.0001);
    }

    static Stream<Arguments> priceCases() {
        return Stream.of(
                arguments(100.0, "VIP", 80.0),
                arguments(100.0, "INTERN", 90.0),
                arguments(100.0, "NORMAL", 100.0),
                arguments(200.0, " vip ", 160.0)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "SUPER"})
    void shouldThrowForInvalidLevel(String level) {
        assertThrows(IllegalArgumentException.class,
                () -> discountCalculator.calculateFinalPrice(100.0, level));
    }
}
