package com.ems.service;

import com.ems.dto.DashboardStatsDto;
import com.ems.dto.EmployeeDto;
import com.ems.entity.Employee;

import java.util.List;

/**
 * Service interface defining business operations for Employee Management.
 * Adheres to Dependency Inversion and Clean Architecture.
 */
public interface EmployeeService {

    /**
     * Retrieve all employees ordered by latest first.
     */
    List<Employee> getAllEmployees();

    /**
     * Retrieve a single employee by primary key ID.
     * Throws EmployeeNotFoundException if not found.
     */
    Employee getEmployeeById(Long id);

    /**
     * Create and persist a new employee from form DTO.
     */
    Employee createEmployee(EmployeeDto employeeDto);

    /**
     * Update an existing employee by ID.
     */
    Employee updateEmployee(Long id, EmployeeDto employeeDto);

    /**
     * Delete an employee by ID.
     */
    void deleteEmployee(Long id);

    /**
     * Search employees by keyword and filter by department.
     */
    List<Employee> searchAndFilterEmployees(String keyword, String department);

    /**
     * Search employees with custom column sorting.
     */
    List<Employee> searchAndFilterEmployees(String keyword, String department, String sortBy, String sortDir);

    /**
     * Get a sorted list of all unique departments.
     */
    List<String> getAllDepartments();

    /**
     * Get calculated statistics, salary analytics, and aggregated metrics for the dashboard.
     */
    DashboardStatsDto getDashboardStats();

    /**
     * Check if an email address is already in use by another employee.
     */
    boolean isEmailTaken(String email, Long excludeId);

    /**
     * Export a list of employees to CSV byte content.
     */
    byte[] exportEmployeesToCsv(List<Employee> employees);
}
