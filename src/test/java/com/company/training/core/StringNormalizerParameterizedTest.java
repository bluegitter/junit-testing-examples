package com.company.training.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("StringNormalizer 参数化测试示例")
class StringNormalizerParameterizedTest {

    private final StringNormalizer normalizer = new StringNormalizer();

    @ParameterizedTest(name = "输入 {0}，输出应为 {1}")
    @CsvSource({
            "'  alice  ', Alice",
            "'BOB', Bob",
            "'  cHaRlIe', Charlie"
    })
    void shouldNormalizeCommonNames(String input, String expected) {
        assertEquals(expected, normalizer.normalizeName(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldReturnUnknownForBlank(String input) {
        assertEquals("UNKNOWN", normalizer.normalizeName(input));
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowForNull(String input) {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalizeName(input));
    }
}
