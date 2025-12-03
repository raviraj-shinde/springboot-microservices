package com.ravi.department_service.model;


public record Employee(
        Long id,
        String name,
        Long departmentId
) {}
