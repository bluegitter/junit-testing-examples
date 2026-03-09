package com.company.training.spring.service;

import com.company.training.spring.mapper.EmployeeMapper;
import com.company.training.spring.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public Employee create(String name, String email) {
        Employee employee = new Employee(null, name, email);
        employeeMapper.insert(employee);
        return employee;
    }

    public Employee findById(Long id) {
        return employeeMapper.selectById(id);
    }

    public List<Employee> findAll() {
        return employeeMapper.selectAll();
    }

    public int update(Employee employee) {
        return employeeMapper.updateById(employee);
    }

    public int delete(Long id) {
        return employeeMapper.deleteById(id);
    }
}
