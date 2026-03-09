package com.company.training.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Spring Service 单元测试示例")
class GreetingServiceTest {

    private final GreetingService greetingService = new GreetingService();

    @Test
    void shouldReturnDefaultGreetingForBlankName() {
        assertEquals("Hello, Guest", greetingService.buildGreeting("  "));
    }

    @Test
    void shouldReturnGreetingForGivenName() {
        assertEquals("Hello, Alice", greetingService.buildGreeting("Alice"));
    }
}
