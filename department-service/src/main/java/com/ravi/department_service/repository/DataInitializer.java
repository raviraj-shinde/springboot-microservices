package com.ravi.department_service.repository;

import com.ravi.department_service.model.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        departmentRepository.addDepartment(new Department(0L, "Mechanical"));
        departmentRepository.addDepartment(new Department(0L, "Information Technology"));
    }
}
