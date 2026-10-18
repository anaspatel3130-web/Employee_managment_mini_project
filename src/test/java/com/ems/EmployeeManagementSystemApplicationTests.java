package com.ems;

import com.ems.dto.EmployeeDto;
import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeManagementSystemApplicationTests {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void contextLoads() {
        assertNotNull(employeeService, "EmployeeService should be loaded into application context");
    }

    @Test
    void testInitialDataSeededAndStatsComputed() {
        List<Employee> employees = employeeService.getAllEmployees();
        assertFalse(employees.isEmpty(), "Initial sample data should be seeded");
        assertTrue(employees.size() >= 10, "Should have at least 10 sample employees");

        var stats = employeeService.getDashboardStats();
        assertNotNull(stats);
        assertTrue(stats.getTotalEmployees() >= 10);
        assertTrue(stats.getTotalDepartments() >= 5);
        assertTrue(stats.getAverageSalary().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(stats.getMonthlyPayroll().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(stats.getDepartmentPercentages());
    }

    @Test
    void testSearchAndFilter() {
        List<Employee> engineerResults = employeeService.searchAndFilterEmployees("Engineering", null);
        assertFalse(engineerResults.isEmpty());

        List<Employee> designDept = employeeService.searchAndFilterEmployees(null, "Design");
        assertFalse(designDept.isEmpty());
        for (Employee emp : designDept) {
            assertEquals("Design", emp.getDepartment());
        }
    }

    @Test
    void testSorting() {
        List<Employee> sortedBySalaryAsc = employeeService.searchAndFilterEmployees(null, null, "salary", "asc");
        assertFalse(sortedBySalaryAsc.isEmpty());
        if (sortedBySalaryAsc.size() > 1) {
            assertTrue(sortedBySalaryAsc.get(0).getSalary().compareTo(sortedBySalaryAsc.get(sortedBySalaryAsc.size() - 1).getSalary()) <= 0);
        }
    }

    @Test
    void testCsvExport() {
        List<Employee> employees = employeeService.getAllEmployees();
        byte[] csvBytes = employeeService.exportEmployeesToCsv(employees);
        assertNotNull(csvBytes);
        String csv = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csv.contains("Employee ID,First Name,Last Name"));
        assertTrue(csv.contains("Engineering"));
    }

    @Test
    void testCreateAndFindEmployee() {
        String uniqueEmail = "test." + System.currentTimeMillis() + "@techcorp.io";
        EmployeeDto dto = new EmployeeDto(
                null,
                "TestFirst",
                "TestLast",
                uniqueEmail,
                "+1 555-999-0000",
                "Engineering",
                "QA Engineer",
                new BigDecimal("85000.00"),
                LocalDate.now()
        );

        Employee created = employeeService.createEmployee(dto);
        assertNotNull(created.getId());
        assertEquals("TestFirst", created.getFirstName());
        assertEquals("TestLast", created.getLastName());

        Employee found = employeeService.getEmployeeById(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals(uniqueEmail, found.getEmail());

        // Cleanup
        employeeService.deleteEmployee(created.getId());
    }
}
