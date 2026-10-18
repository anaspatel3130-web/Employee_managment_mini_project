package com.ems.repository;

import com.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * EmployeeRepository provides the data access layer for Employee entities.
 * 
 * By extending JpaRepository<Employee, Long>, Spring Data JPA automatically provides
 * standard CRUD methods (save, findById, findAll, deleteById, count, etc.)
 * without needing to write any SQL or boilerplate implementation.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Search and filter employees simultaneously by keyword and department.
     * Searches across first name, last name, full name, email, and job title.
     * Orders results with most recently added employees first.
     */
    @Query("SELECT e FROM Employee e WHERE " +
           "(:department IS NULL OR :department = '' OR LOWER(e.department) = LOWER(:department)) AND " +
           "(:keyword IS NULL OR :keyword = '' OR (" +
           "  LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
           ")) ORDER BY e.id DESC")
    List<Employee> searchAndFilterEmployees(@Param("keyword") String keyword, 
                                           @Param("department") String department);

    /**
     * Find top 5 most recently created employees for the dashboard.
     */
    List<Employee> findTop5ByOrderByIdDesc();

    /**
     * Get a distinct sorted list of all active departments.
     */
    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department ASC")
    List<String> findDistinctDepartments();

    /**
     * Count the number of distinct departments.
     */
    @Query("SELECT COUNT(DISTINCT e.department) FROM Employee e")
    long countDistinctDepartments();

    /**
     * Calculate average salary across all employees.
     */
    @Query("SELECT AVG(e.salary) FROM Employee e")
    BigDecimal calculateAverageSalary();

    /**
     * Calculate total monthly payroll across all employees.
     */
    @Query("SELECT SUM(e.salary) FROM Employee e")
    BigDecimal calculateTotalPayroll();

    /**
     * Check if an email already exists (for create validation).
     */
    boolean existsByEmail(String email);

    /**
     * Check if an email exists for a different employee (for update validation).
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
