package com.ems.config;

import com.ems.entity.Employee;
import com.ems.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DataInitializer executes immediately upon application startup.
 * 
 * If the database table is empty, it seeds initial sample employees so that
 * the dashboard and tables are fully interactive from day one.
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final EmployeeRepository employeeRepository;

    public DataInitializer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            log.info("No employee records found in database. Seeding initial sample data...");

            List<Employee> sampleEmployees = List.of(
                new Employee("Sarah", "Jenkins", "sarah.jenkins@techcorp.io", "+1 (555) 234-5678",
                        "Engineering", "Principal Cloud Architect", new BigDecimal("135000.00"), LocalDate.of(2021, 3, 15)),

                new Employee("Marcus", "Vance", "marcus.vance@techcorp.io", "+1 (555) 345-6789",
                        "Engineering", "Senior Full Stack Engineer", new BigDecimal("112000.00"), LocalDate.of(2022, 6, 1)),

                new Employee("Elena", "Rostova", "elena.rostova@techcorp.io", "+1 (555) 456-7890",
                        "Product Management", "Lead Product Manager", new BigDecimal("125000.00"), LocalDate.of(2022, 1, 10)),

                new Employee("Devon", "Miller", "devon.miller@techcorp.io", "+1 (555) 567-8901",
                        "Design", "Senior UX/UI Designer", new BigDecimal("98000.00"), LocalDate.of(2023, 2, 20)),

                new Employee("Amina", "Khatib", "amina.khatib@techcorp.io", "+1 (555) 678-9012",
                        "Human Resources", "People Operations Director", new BigDecimal("105000.00"), LocalDate.of(2020, 11, 5)),

                new Employee("Lucas", "Moretti", "lucas.moretti@techcorp.io", "+1 (555) 789-0123",
                        "Finance", "Senior Financial Analyst", new BigDecimal("92000.00"), LocalDate.of(2023, 5, 12)),

                new Employee("Chloe", "Dupont", "chloe.dupont@techcorp.io", "+1 (555) 890-1234",
                        "Marketing", "Growth Marketing Lead", new BigDecimal("89000.00"), LocalDate.of(2023, 8, 1)),

                new Employee("Jason", "Zhang", "jason.zhang@techcorp.io", "+1 (555) 901-2345",
                        "Engineering", "DevOps & SRE Specialist", new BigDecimal("108000.00"), LocalDate.of(2023, 9, 15)),

                new Employee("Rachel", "Green", "rachel.green@techcorp.io", "+1 (555) 012-3456",
                        "Sales", "Enterprise Account Executive", new BigDecimal("95000.00"), LocalDate.of(2024, 1, 8)),

                new Employee("Tariq", "Mansoor", "tariq.mansoor@techcorp.io", "+1 (555) 123-4567",
                        "Customer Support", "Customer Success Lead", new BigDecimal("78000.00"), LocalDate.of(2024, 3, 1))
            );

            employeeRepository.saveAll(sampleEmployees);
            log.info("Successfully seeded {} sample employees into H2 database.", sampleEmployees.size());
        } else {
            log.info("Database already contains {} employee records. Skipping initial seeding.", employeeRepository.count());
        }
    }
}
