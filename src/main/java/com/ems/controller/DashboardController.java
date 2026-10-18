package com.ems.controller;

import com.ems.dto.DashboardStatsDto;
import com.ems.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling dashboard metrics, statistics, settings, and top-level navigation.
 */
@Controller
public class DashboardController {

    private final EmployeeService employeeService;

    public DashboardController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Root URL redirects directly to the main dashboard.
     */
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/dashboard";
    }

    /**
     * Dashboard page showing high-level stats, department breakdown, and recent employees.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        DashboardStatsDto stats = employeeService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    /**
     * Settings page allowing users to customize themes (Light / Neon Black),
     * review environment specifications, and manage application configurations.
     */
    @GetMapping("/settings")
    public String showSettings(Model model) {
        DashboardStatsDto stats = employeeService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("activePage", "settings");
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("osName", System.getProperty("os.name"));
        return "settings";
    }
}
