package com.company.training.spring.mapper;

import com.company.training.spring.model.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Insert("INSERT INTO employees(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Employee employee);

    @Update("UPDATE employees SET name = #{name}, email = #{email} WHERE id = #{id}")
    int updateById(Employee employee);

    @Delete("DELETE FROM employees WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, name, email FROM employees WHERE id = #{id}")
    Employee selectById(Long id);

    @Select("SELECT id, name, email FROM employees ORDER BY id")
    List<Employee> selectAll();
}
