package com.ems.exception;

/**
 * Custom runtime exception thrown when an employee with a specific ID is not found.
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with ID: " + id);
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
