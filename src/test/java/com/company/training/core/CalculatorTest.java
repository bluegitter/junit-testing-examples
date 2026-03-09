package com.company.training.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Calculator 基础断言示例")
class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    @DisplayName("加法：正数相加")
    void shouldAddTwoPositiveNumbers() {
        assertEquals(7, calculator.add(3, 4));
    }

    @Test
    @DisplayName("加法：整数上边界与 0 相加")
    void shouldKeepMaxValueWhenAddZero() {
        assertEquals(Integer.MAX_VALUE, calculator.add(Integer.MAX_VALUE, 0));
    }

    @Test
    @DisplayName("除法：除数为 0 抛出异常")
    void shouldThrowWhenDivideByZero() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(10, 0)
        );
        assertEquals("Divider must not be zero", ex.getMessage());
    }
}
