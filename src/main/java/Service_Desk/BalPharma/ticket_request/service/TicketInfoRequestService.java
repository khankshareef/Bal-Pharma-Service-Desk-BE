package Service_Desk.BalPharma.ticket_request.service;

import Service_Desk.BalPharma.ticket_request.dto.CreateTicketInfoRequestDto;
import Service_Desk.BalPharma.ticket_request.dto.RespondTicketInfoRequestDto;
import Service_Desk.BalPharma.ticket_request.dto.TicketInfoRequestResponseDto;

import java.util.List;

public interface TicketInfoRequestService {
    TicketInfoRequestResponseDto create(CreateTicketInfoRequestDto dto, String employeeId);
    TicketInfoRequestResponseDto getById(Long id);
    List<TicketInfoRequestResponseDto> getAll();
    List<TicketInfoRequestResponseDto> getByTicket(Long ticketId);
    List<TicketInfoRequestResponseDto> getByRequester(String employeeId);
    TicketInfoRequestResponseDto respond(Long id, RespondTicketInfoRequestDto dto, String employeeId);
    TicketInfoRequestResponseDto close(Long id, String employeeId);
    void delete(Long id);

    List<TicketInfoRequestResponseDto> getPendingForUser(String employeeId);
}