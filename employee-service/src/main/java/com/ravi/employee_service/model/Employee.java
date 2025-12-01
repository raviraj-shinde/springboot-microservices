package com.ravi.employee_service.model;


public record Employee(
        Long id,
        String name,
        Long departmentId
) {}
