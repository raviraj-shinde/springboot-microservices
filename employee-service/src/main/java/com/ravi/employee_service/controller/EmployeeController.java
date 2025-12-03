package com.ravi.employee_service.controller;

import com.ravi.employee_service.model.Employee;
import com.ravi.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    //not recommended to use in controller
    private final EmployeeRepository employeeRepo;

    @PostMapping
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee){
        LOGGER.info("Called Employee - addEmployee()");
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeRepo.save(employee));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> findAll(){
        LOGGER.info("Called Employee - findAll()");
        return ResponseEntity.ok().body(employeeRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable Long id){
        LOGGER.info("Called Employee - findById({})", id);
        return ResponseEntity.status(HttpStatus.FOUND).body(employeeRepo.findById(id));
    }

    @GetMapping("/department/{departmentId}")
    public List<Employee> findByDepartment(@PathVariable Long departmentId){
        LOGGER.info("Called Employee - findByDepartment({})", departmentId);
        return employeeRepo.findByDepartment(departmentId);
    }

 }
