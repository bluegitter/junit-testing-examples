package com.company.training.spring.controller;

import com.company.training.spring.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class GreetingController {

    /**
     * 获取欢迎信息服务
     */
    private final GreetingService greetingService;

    /**
     * 构造函数
     *
     * @param greetingService 获取欢迎信息服务
     */
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * 获取欢迎信息
     *
     * @param name 用户名
     * @return 欢迎信息
     */
    @GetMapping("/api/greeting")
    public Map<String, String> greeting(@RequestParam(required = false) String name) {
        return Collections.singletonMap("message", greetingService.buildGreeting(name));
    }
}
