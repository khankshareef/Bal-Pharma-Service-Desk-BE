package Service_Desk.BalPharma.ticket.service;

import Service_Desk.BalPharma.reopen.dto.CreateReopenDto;
import Service_Desk.BalPharma.ticket.dto.*;
import Service_Desk.BalPharma.reopen.dto.CreateReopenDto;
import Service_Desk.BalPharma.reopen.dto.TicketReopenResponseDto;

import java.util.List;

public interface TicketService {
    TicketResponseDto create(CreateTicketDto dto, String employeeId);
    List<TicketResponseDto> getAll();
    List<TicketResponseDto> getByEmployee(String employeeId);
    TicketResponseDto getById(Long id);
    TicketResponseDto update(Long id, UpdateTicketDto dto, String employeeId);
    TicketResponseDto updateStatus(Long id, UpdateTicketStatusDto dto, String employeeId);
    void delete(Long id);
    TicketStatsDto getStats();
    TicketResponseDto assignTicket(Long id, AssignTicketDto dto, String assignedByEmployeeId);
    List<TicketResponseDto> getAssignedToExecutive(String employeeId);
    List<TicketResponseDto> getUnassignedOpenTickets();
    SlaDashboardDto getSlaDashboard(String employeeId);
    TicketStatsDto getStatsForExecutive(String employeeId);
    TicketReopenResponseDto requestReopen(Long ticketId, CreateReopenDto dto, String employeeId);


}