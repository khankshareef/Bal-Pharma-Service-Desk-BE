package Service_Desk.BalPharma.reopen.service;

import Service_Desk.BalPharma.reopen.dto.*;

import java.util.List;

public interface ReopenService {

    ReopenResponseDto requestReopen(CreateReopenDto dto, String employeeId);
    List<ReopenResponseDto> getAll();
    List<ReopenResponseDto> getPending();
    List<ReopenResponseDto> getByTicket(Long ticketId);
    ReopenResponseDto getById(Long id);
    ReopenResponseDto review(Long id, ReviewReopenDto dto, String reviewerEmployeeId);
}
