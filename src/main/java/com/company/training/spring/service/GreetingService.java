package com.company.training.spring.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String buildGreeting(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, Guest";
        }
        return "Hello, " + name.trim();
    }
}
