package com.ravi.employee_service.service;

import com.ravi.employee_service.model.Employee;
import com.ravi.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitialization implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data initialization start....");

        employeeRepository.save(new Employee(0L, "Egris", 1L));
        employeeRepository.save(new Employee(1L, "John", 1L));

        log.info("Data initialization end....");
    }
}
