package com.ems.service.impl;

import com.ems.dto.DashboardStatsDto;
import com.ems.dto.EmployeeDto;
import com.ems.entity.Employee;
import com.ems.exception.EmployeeNotFoundException;
import com.ems.repository.EmployeeRepository;
import com.ems.service.EmployeeService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of EmployeeService containing business rules, analytics, and orchestration.
 */
@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    @Transactional
    public Employee createEmployee(EmployeeDto employeeDto) {
        if (employeeDto.getEmail() != null && employeeRepository.existsByEmail(employeeDto.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("An employee with email '" + employeeDto.getEmail() + "' already exists.");
        }

        Employee employee = employeeDto.toEntity();
        employee.setId(null);
        // Normalize email to lowercase
        if (employee.getEmail() != null) {
            employee.setEmail(employee.getEmail().trim().toLowerCase());
        }
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeDto employeeDto) {
        Employee existingEmployee = getEmployeeById(id);

        String normalizedEmail = employeeDto.getEmail() != null ? employeeDto.getEmail().trim().toLowerCase() : "";
        if (employeeRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new IllegalArgumentException("Email '" + employeeDto.getEmail() + "' is already in use by another employee.");
        }

        existingEmployee.setFirstName(employeeDto.getFirstName().trim());
        existingEmployee.setLastName(employeeDto.getLastName().trim());
        existingEmployee.setEmail(normalizedEmail);
        existingEmployee.setPhone(employeeDto.getPhone().trim());
        existingEmployee.setDepartment(employeeDto.getDepartment().trim());
        existingEmployee.setJobTitle(employeeDto.getJobTitle().trim());
        existingEmployee.setSalary(employeeDto.getSalary());
        existingEmployee.setJoiningDate(employeeDto.getJoiningDate());

        return employeeRepository.save(existingEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public List<Employee> searchAndFilterEmployees(String keyword, String department) {
        return searchAndFilterEmployees(keyword, department, "id", "desc");
    }

    @Override
    public List<Employee> searchAndFilterEmployees(String keyword, String department, String sortBy, String sortDir) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanDepartment = (department != null && !department.trim().isEmpty()) ? department.trim() : null;

        List<Employee> results;
        if (cleanKeyword == null && cleanDepartment == null) {
            results = new ArrayList<>(employeeRepository.findAll());
        } else {
            results = new ArrayList<>(employeeRepository.searchAndFilterEmployees(cleanKeyword, cleanDepartment));
        }

        // Apply in-memory dynamic sorting for interview-grade responsiveness
        Comparator<Employee> comparator;
        String sortField = sortBy != null ? sortBy.toLowerCase() : "id";
        boolean ascending = "asc".equalsIgnoreCase(sortDir);

        switch (sortField) {
            case "name":
                comparator = Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "department":
                comparator = Comparator.comparing(Employee::getDepartment, String.CASE_INSENSITIVE_ORDER);
                break;
            case "salary":
                comparator = Comparator.comparing(Employee::getSalary);
                break;
            case "joiningdate":
                comparator = Comparator.comparing(Employee::getJoiningDate);
                break;
            case "id":
            default:
                comparator = Comparator.comparing(Employee::getId);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        results.sort(comparator);
        return results;
    }

    @Override
    public List<String> getAllDepartments() {
        return employeeRepository.findDistinctDepartments();
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        long totalEmployees = employeeRepository.count();
        long totalDepartments = employeeRepository.countDistinctDepartments();

        BigDecimal rawAverageSalary = employeeRepository.calculateAverageSalary();
        BigDecimal averageSalary = rawAverageSalary != null 
                ? rawAverageSalary.setScale(2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO.setScale(2);

        BigDecimal rawTotalPayroll = employeeRepository.calculateTotalPayroll();
        BigDecimal totalPayroll = rawTotalPayroll != null 
                ? rawTotalPayroll.setScale(2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO.setScale(2);

        BigDecimal monthlyPayroll = totalPayroll.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        List<Employee> allEmployees = employeeRepository.findAll();

        BigDecimal highestSalary = allEmployees.stream()
                .map(Employee::getSalary)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        BigDecimal lowestSalary = allEmployees.stream()
                .map(Employee::getSalary)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        List<Employee> recentEmployees = employeeRepository.findTop5ByOrderByIdDesc();
        List<String> departments = employeeRepository.findDistinctDepartments();

        Map<String, Long> departmentCounts = allEmployees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.counting()));

        Map<String, Double> departmentPercentages = new LinkedHashMap<>();
        if (totalEmployees > 0) {
            departmentCounts.forEach((dept, count) -> {
                double pct = Math.round((count * 100.0 / totalEmployees) * 10.0) / 10.0;
                departmentPercentages.put(dept, pct);
            });
        }

        return new DashboardStatsDto(
                totalEmployees,
                totalDepartments,
                averageSalary,
                totalPayroll,
                monthlyPayroll,
                highestSalary,
                lowestSalary,
                recentEmployees,
                departments,
                departmentCounts,
                departmentPercentages
        );
    }

    @Override
    public boolean isEmailTaken(String email, Long excludeId) {
        if (email == null) return false;
        String normalized = email.trim().toLowerCase();
        if (excludeId == null) {
            return employeeRepository.existsByEmail(normalized);
        }
        return employeeRepository.existsByEmailAndIdNot(normalized, excludeId);
    }

    @Override
    public byte[] exportEmployeesToCsv(List<Employee> employees) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            // Write CSV Header
            writer.println("Employee ID,First Name,Last Name,Email,Phone,Department,Job Title,Salary (USD),Joining Date");

            // Write Employee rows
            for (Employee emp : employees) {
                writer.printf("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%.2f\",\"%s\"%n",
                        emp.getId(),
                        escapeCsv(emp.getFirstName()),
                        escapeCsv(emp.getLastName()),
                        escapeCsv(emp.getEmail()),
                        escapeCsv(emp.getPhone()),
                        escapeCsv(emp.getDepartment()),
                        escapeCsv(emp.getJobTitle()),
                        emp.getSalary(),
                        emp.getJoiningDate()
                );
            }
        }
        return out.toByteArray();
    }

    private String escapeCsv(String value) {
        return value != null ? value.replace("\"", "\"\"") : "";
    }
}
