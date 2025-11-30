package com.ravi.department_service.model;


public record Employee(
        Long id,
        Long departmentId,
        String name,
        Integer age,
        String position
) {}
