package com.company.training.spring.mapper;

import com.company.training.spring.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@MybatisTest
@Sql(scripts = "/sql/employee-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("MyBatis Mapper 增删查改单元测试")
class EmployeeMapperMybatisTest {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    void shouldInsertAndSelectById() {
        Employee employee = new Employee(null, "Tom", "tom@company.com");

        int inserted = employeeMapper.insert(employee);
        assertEquals(1, inserted);
        assertNotNull(employee.getId());

        Employee dbEmployee = employeeMapper.selectById(employee.getId());
        assertNotNull(dbEmployee);
        assertEquals("Tom", dbEmployee.getName());
        assertEquals("tom@company.com", dbEmployee.getEmail());
    }

    @Test
    void shouldUpdateById() {
        Employee employee = new Employee(null, "Jack", "jack@company.com");
        employeeMapper.insert(employee);

        employee.setName("Jacky");
        employee.setEmail("jacky@company.com");

        int updated = employeeMapper.updateById(employee);
        assertEquals(1, updated);

        Employee dbEmployee = employeeMapper.selectById(employee.getId());
        assertEquals("Jacky", dbEmployee.getName());
        assertEquals("jacky@company.com", dbEmployee.getEmail());
    }

    @Test
    void shouldDeleteById() {
        Employee employee = new Employee(null, "Rose", "rose@company.com");
        employeeMapper.insert(employee);

        int deleted = employeeMapper.deleteById(employee.getId());
        assertEquals(1, deleted);

        Employee dbEmployee = employeeMapper.selectById(employee.getId());
        assertNull(dbEmployee);
    }

    @Test
    void shouldSelectAll() {
        employeeMapper.insert(new Employee(null, "A", "a@company.com"));
        employeeMapper.insert(new Employee(null, "B", "b@company.com"));

        List<Employee> employees = employeeMapper.selectAll();
        assertEquals(2, employees.size());
    }
}
