package com.ravi.employee_service.repository;

import com.ravi.employee_service.model.Employee;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepository {
    List<Employee> employees = new ArrayList<>();

    public Employee save(Employee employee){
        employees.add(employee);
        return employee;
    }

    public List<Employee> findAll(){
        return employees;
    }

    public Employee findById(Long id){
        return employees.stream()
                .filter((e) -> e.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Employee not found by id: " + id));
    }

    public List<Employee> findByDepartment(Long id){
        return employees.stream()
                .filter((e) -> e.departmentId().equals(id))
                .collect(Collectors.toList());
    }
}
