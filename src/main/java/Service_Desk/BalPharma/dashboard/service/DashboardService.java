package Service_Desk.BalPharma.dashboard.service;

import Service_Desk.BalPharma.dashboard.dto.DashboardResponseDto;
import Service_Desk.BalPharma.dashboard.dto.TicketStatusOverviewDto;

public interface DashboardService {

    DashboardResponseDto getDashboard();

    DashboardResponseDto getEmployeeDashboard(String employeeId);

    DashboardResponseDto getExecutiveDashboard(String employeeId);

    TicketStatusOverviewDto getStatusOverview();

    TicketStatusOverviewDto getEmployeeStatusOverview(String employeeId);

    TicketStatusOverviewDto getExecutiveStatusOverview(String employeeId);
}