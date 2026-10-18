package com.ems.dto;

import com.ems.entity.Employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for Dashboard statistics and aggregated figures.
 * 
 * Decouples presentation analytics from entity models.
 */
public class DashboardStatsDto {

    private long totalEmployees;
    private long totalDepartments;
    private BigDecimal averageSalary;
    private BigDecimal totalPayroll;
    private BigDecimal monthlyPayroll;
    private BigDecimal highestSalary;
    private BigDecimal lowestSalary;
    private List<Employee> recentEmployees;
    private List<String> departments;
    private Map<String, Long> departmentEmployeeCounts;
    private Map<String, Double> departmentPercentages;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalEmployees, long totalDepartments, BigDecimal averageSalary,
                             BigDecimal totalPayroll, BigDecimal monthlyPayroll,
                             BigDecimal highestSalary, BigDecimal lowestSalary,
                             List<Employee> recentEmployees, List<String> departments,
                             Map<String, Long> departmentEmployeeCounts,
                             Map<String, Double> departmentPercentages) {
        this.totalEmployees = totalEmployees;
        this.totalDepartments = totalDepartments;
        this.averageSalary = averageSalary;
        this.totalPayroll = totalPayroll;
        this.monthlyPayroll = monthlyPayroll;
        this.highestSalary = highestSalary;
        this.lowestSalary = lowestSalary;
        this.recentEmployees = recentEmployees;
        this.departments = departments;
        this.departmentEmployeeCounts = departmentEmployeeCounts;
        this.departmentPercentages = departmentPercentages;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public BigDecimal getTotalPayroll() {
        return totalPayroll;
    }

    public void setTotalPayroll(BigDecimal totalPayroll) {
        this.totalPayroll = totalPayroll;
    }

    public BigDecimal getMonthlyPayroll() {
        return monthlyPayroll;
    }

    public void setMonthlyPayroll(BigDecimal monthlyPayroll) {
        this.monthlyPayroll = monthlyPayroll;
    }

    public BigDecimal getHighestSalary() {
        return highestSalary;
    }

    public void setHighestSalary(BigDecimal highestSalary) {
        this.highestSalary = highestSalary;
    }

    public BigDecimal getLowestSalary() {
        return lowestSalary;
    }

    public void setLowestSalary(BigDecimal lowestSalary) {
        this.lowestSalary = lowestSalary;
    }

    public List<Employee> getRecentEmployees() {
        return recentEmployees;
    }

    public void setRecentEmployees(List<Employee> recentEmployees) {
        this.recentEmployees = recentEmployees;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public Map<String, Long> getDepartmentEmployeeCounts() {
        return departmentEmployeeCounts;
    }

    public void setDepartmentEmployeeCounts(Map<String, Long> departmentEmployeeCounts) {
        this.departmentEmployeeCounts = departmentEmployeeCounts;
    }

    public Map<String, Double> getDepartmentPercentages() {
        return departmentPercentages;
    }

    public void setDepartmentPercentages(Map<String, Double> departmentPercentages) {
        this.departmentPercentages = departmentPercentages;
    }
}
