package com.company.training.spring;

import com.company.training.spring.metrics.TestMetricsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.company.training.spring")
@EnableScheduling
@EnableConfigurationProperties(TestMetricsProperties.class)
public class SpringBootTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTrainingApplication.class, args);
    }
}
