package com.ems.dto;

import com.ems.entity.Employee;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EmployeeDto carries form data between the browser and the controller.
 * 
 * Using a DTO prevents exposing the raw database entity directly to HTTP form submissions,
 * which is a recommended Spring Boot security and software design best practice.
 */
public class EmployeeDto {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address (e.g., name@company.com)")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+0-9\\s\\-()]{7,20}$", message = "Please enter a valid phone number (7-20 digits)")
    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Job title is required")
    @Size(min = 2, max = 100, message = "Job title must be between 2 and 100 characters")
    private String jobTitle;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "1.00", message = "Salary must be at least 1.00")
    @Digits(integer = 10, fraction = 2, message = "Salary must have up to 10 integer digits and 2 decimal places")
    private BigDecimal salary;

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joiningDate;

    public EmployeeDto() {
    }

    public EmployeeDto(Long id, String firstName, String lastName, String email, String phone,
                       String department, String jobTitle, BigDecimal salary, LocalDate joiningDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.joiningDate = joiningDate;
    }

    /**
     * Factory method to convert an Employee entity into an EmployeeDto.
     */
    public static EmployeeDto fromEntity(Employee employee) {
        if (employee == null) return null;
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getSalary(),
                employee.getJoiningDate()
        );
    }

    /**
     * Convert this DTO into an Employee entity.
     */
    public Employee toEntity() {
        Employee employee = new Employee();
        employee.setId(this.id);
        employee.setFirstName(this.firstName);
        employee.setLastName(this.lastName);
        employee.setEmail(this.email);
        employee.setPhone(this.phone);
        employee.setDepartment(this.department);
        employee.setJobTitle(this.jobTitle);
        employee.setSalary(this.salary);
        employee.setJoiningDate(this.joiningDate);
        return employee;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
}
