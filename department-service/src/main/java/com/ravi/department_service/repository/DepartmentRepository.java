package com.ravi.department_service.repository;

import com.ravi.department_service.model.Department;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DepartmentRepository {
    List<Department> departmentList = new ArrayList<>();

    public Department addDepartment(Department department){
        departmentList.add(department);
        return department;
    }

    public Department findById(Long id){
        return departmentList.stream()
                .filter((department) -> department.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Department not found by id: " + id));
    }

    public List<Department> findAll(){
        return departmentList;
    }
}
