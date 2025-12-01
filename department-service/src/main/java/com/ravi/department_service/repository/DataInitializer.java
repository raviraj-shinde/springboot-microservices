package com.ravi.department_service.repository;

import com.ravi.department_service.model.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data initialization start...");
        long start = System.nanoTime();

        departmentRepository.addDepartment(new Department(0L, "Mechanical"));
        departmentRepository.addDepartment(new Department(1L, "Information Technology"));

        long duration = System.nanoTime()-start;
        log.info("Data initialization eended in : {} ns", duration);
    }
}
