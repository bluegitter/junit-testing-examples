package com.company.training.spring.controller;

import com.company.training.spring.service.GreetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GreetingController.class)
@DisplayName("Spring WebMvcTest 示例")
class GreetingControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GreetingService greetingService;

    @Test
    void shouldReturnGreetingMessage() throws Exception {
        when(greetingService.buildGreeting("Bob")).thenReturn("Hello, Bob");

        mockMvc.perform(get("/api/greeting").param("name", "Bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Bob"));
    }

    @Test
    void shouldReturnDefaultGreetingWhenNameMissing() throws Exception {
        when(greetingService.buildGreeting(isNull())).thenReturn("Hello, Guest");

        mockMvc.perform(get("/api/greeting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, Guest"));
    }
}
