package Service_Desk.BalPharma.dashboard.controller;

import Service_Desk.BalPharma.dashboard.dto.DashboardResponseDto;
import Service_Desk.BalPharma.dashboard.dto.TicketStatusOverviewDto;
import Service_Desk.BalPharma.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/status-overview")
    public ResponseEntity<TicketStatusOverviewDto> getStatusOverview() {
        return ResponseEntity.ok(dashboardService.getStatusOverview());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<DashboardResponseDto> getEmployeeDashboard(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(dashboardService.getEmployeeDashboard(employeeId));
    }

    @GetMapping("/status-overview/employee/{employeeId}")
    public ResponseEntity<TicketStatusOverviewDto> getEmployeeStatusOverview(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(dashboardService.getEmployeeStatusOverview(employeeId));
    }


    @GetMapping("/executive/{employeeId}")
    public ResponseEntity<DashboardResponseDto> getExecutiveDashboard(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(dashboardService.getExecutiveDashboard(employeeId));
    }

    @GetMapping("/status-overview/executive/{employeeId}")
    public ResponseEntity<TicketStatusOverviewDto> getExecutiveStatusOverview(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(dashboardService.getExecutiveStatusOverview(employeeId));
    }
}