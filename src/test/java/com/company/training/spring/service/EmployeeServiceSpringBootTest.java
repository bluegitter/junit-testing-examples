package com.company.training.spring.service;

import com.company.training.spring.SpringBootTrainingApplication;
import com.company.training.spring.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = SpringBootTrainingApplication.class)
@Sql(scripts = "/sql/employee-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("SpringBoot + MyBatis Service 层测试")
class EmployeeServiceSpringBootTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void shouldCompleteCrudFlow() {
        Employee created = employeeService.create("Lily", "lily@company.com");
        assertNotNull(created.getId());

        Employee fromDb = employeeService.findById(created.getId());
        assertEquals("Lily", fromDb.getName());

        fromDb.setName("Lily Chen");
        int updated = employeeService.update(fromDb);
        assertEquals(1, updated);

        List<Employee> all = employeeService.findAll();
        assertEquals(1, all.size());
        assertEquals("Lily Chen", all.get(0).getName());

        int deleted = employeeService.delete(created.getId());
        assertEquals(1, deleted);
        assertNull(employeeService.findById(created.getId()));
    }
}
