package com.ravi.department_service.controller;

import com.ravi.department_service.client.EmployeeClient;
import com.ravi.department_service.model.Department;
import com.ravi.department_service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/department")
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeClient employeeClient;

    @PostMapping
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        LOGGER.info("Department add: {}", department);
        return ResponseEntity.ok().body(departmentRepository.addDepartment(department));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        LOGGER.info("Department findAll called");
        return ResponseEntity.status(HttpStatus.FOUND).body(departmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        LOGGER.info("Department findById= {}", id);
        return ResponseEntity.status(HttpStatus.FOUND).body(departmentRepository.findById(id));
    }

    @GetMapping("/{deptId}/employees")
    public ResponseEntity<Department> getEmployeesByDepartment(@PathVariable Long deptId){
        LOGGER.info("getEmployeesByDepartment department id = {}", deptId);
        Department department = departmentRepository.findById(deptId);
        department.setEmployees(employeeClient.findByDepartment(deptId));
        return ResponseEntity.ok().body(department);
    }
}

//LOGGER.info(""):- Why we have added? :- for Zipkin
//so that we can track the flow
