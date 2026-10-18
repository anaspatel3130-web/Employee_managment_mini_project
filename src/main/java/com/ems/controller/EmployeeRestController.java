package com.ems.controller;

import com.ems.dto.DashboardStatsDto;
import com.ems.dto.EmployeeDto;
import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller providing programmatic JSON endpoints.
 * 
 * Demonstrates dual-channel architectural capability:
 * Both Thymeleaf HTML views and RESTful JSON APIs are supported simultaneously.
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * GET /api/v1/employees : List all employees with optional search and filter.
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        List<Employee> employees = employeeService.searchAndFilterEmployees(keyword, department, sortBy, sortDir);
        return ResponseEntity.ok(employees);
    }

    /**
     * GET /api/v1/employees/{id} : Get an individual employee by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * GET /api/v1/employees/stats : Get dashboard statistics and salary analytics.
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(employeeService.getDashboardStats());
    }

    /**
     * POST /api/v1/employees : Create an employee via JSON payload.
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        Employee created = employeeService.createEmployee(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/v1/employees/{id} : Update an employee via JSON payload.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable("id") Long id,
            @Valid @RequestBody EmployeeDto employeeDto) {
        Employee updated = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/v1/employees/{id} : Delete an employee by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
