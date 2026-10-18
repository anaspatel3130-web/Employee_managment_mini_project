package com.ems.controller;

import com.ems.dto.EmployeeDto;
import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Controller handling all Employee CRUD operations, search, filtering, sorting, and CSV export.
 */
@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    private static final List<String> DEFAULT_DEPARTMENTS = List.of(
            "Engineering",
            "Product Management",
            "Human Resources",
            "Finance",
            "Marketing",
            "Sales",
            "Customer Support",
            "Operations",
            "Design",
            "Legal"
    );

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    private List<String> getAvailableDepartments() {
        Set<String> departments = new LinkedHashSet<>(DEFAULT_DEPARTMENTS);
        departments.addAll(employeeService.getAllDepartments());
        return new ArrayList<>(departments);
    }

    /**
     * Display the employee list page with multi-field search, department filter, and dynamic column sorting.
     */
    @GetMapping
    public String listEmployees(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        List<Employee> employees = employeeService.searchAndFilterEmployees(keyword, department, sortBy, sortDir);
        List<String> departments = employeeService.getAllDepartments();

        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedDepartment", department != null ? department : "");
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
        model.addAttribute("totalCount", employees.size());
        model.addAttribute("activePage", "employees");

        return "employees/list";
    }

    /**
     * Export currently filtered or all employees directly to CSV.
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToCsv(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        List<Employee> employees = employeeService.searchAndFilterEmployees(keyword, department, sortBy, sortDir);
        byte[] csvData = employeeService.exportEmployeesToCsv(employees);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"apex_employees_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    /**
     * Display form to add a new employee.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new EmployeeDto());
        model.addAttribute("departments", getAvailableDepartments());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "new-employee");
        return "employees/form";
    }

    /**
     * Process creation of a new employee.
     */
    @PostMapping
    public String createEmployee(
            @Valid @ModelAttribute("employee") EmployeeDto employeeDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (employeeDto.getEmail() != null && employeeService.isEmailTaken(employeeDto.getEmail(), null)) {
            bindingResult.rejectValue("email", "error.employee", "An employee with this email address already exists.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", getAvailableDepartments());
            model.addAttribute("isEdit", false);
            model.addAttribute("activePage", "new-employee");
            return "employees/form";
        }

        Employee created = employeeService.createEmployee(employeeDto);
        redirectAttributes.addFlashAttribute("successMessage", 
                "🎉 " + created.getFullName() + " was successfully added to the organization!");

        return "redirect:/employees";
    }

    /**
     * Display form to edit an existing employee.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        EmployeeDto employeeDto = EmployeeDto.fromEntity(employee);

        model.addAttribute("employee", employeeDto);
        model.addAttribute("employeeId", id);
        model.addAttribute("departments", getAvailableDepartments());
        model.addAttribute("isEdit", true);
        model.addAttribute("activePage", "employees");
        return "employees/form";
    }

    /**
     * Process update of existing employee.
     */
    @PostMapping("/update/{id}")
    public String updateEmployee(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("employee") EmployeeDto employeeDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (employeeDto.getEmail() != null && employeeService.isEmailTaken(employeeDto.getEmail(), id)) {
            bindingResult.rejectValue("email", "error.employee", "This email address is already in use by another employee.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("employeeId", id);
            model.addAttribute("departments", getAvailableDepartments());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "employees");
            return "employees/form";
        }

        Employee updated = employeeService.updateEmployee(id, employeeDto);
        redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Profile for " + updated.getFullName() + " was successfully updated!");

        return "redirect:/employees";
    }

    /**
     * Delete an employee with confirmation.
     */
    @PostMapping("/delete/{id}")
    public String deleteEmployee(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        Employee employee = employeeService.getEmployeeById(id);
        String name = employee.getFullName();
        employeeService.deleteEmployee(id);

        redirectAttributes.addFlashAttribute("successMessage", 
                "🗑️ Employee " + name + " (ID: #" + id + ") was removed from the roster.");

        return "redirect:/employees";
    }

    /**
     * Display full details of an individual employee.
     */
    @GetMapping("/{id}")
    public String showEmployeeDetails(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("activePage", "employees");
        return "employees/details";
    }
}
